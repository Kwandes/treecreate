package dev.hotdeals.treecreate.controller;

import dev.hotdeals.treecreate.model.FamilyTreeDesignJSON;
import dev.hotdeals.treecreate.model.TreeDesign;
import dev.hotdeals.treecreate.model.TreeOrder;
import dev.hotdeals.treecreate.model.User;
import dev.hotdeals.treecreate.repository.TreeDesignRepo;
import dev.hotdeals.treecreate.repository.TreeOrderRepo;
import dev.hotdeals.treecreate.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;

import static org.hibernate.bytecode.BytecodeLogger.LOGGER;

@RestController
public class TreeController
{
    @Autowired
    UserRepo userRepo;

    @Autowired
    TreeOrderRepo treeOrderRepo;

    @Autowired
    TreeDesignRepo treeDesignRepo;

    @PostMapping("/addTreeDesign")
    ResponseEntity<String> addTreeDesign(@RequestBody FamilyTreeDesignJSON design)
    {
        LOGGER.info("Adding a new tree design");
        TreeDesign treeDesign = new TreeDesign();
        treeDesign.setDateCreated(LocalDateTime.now().toString());
        treeDesign.setDesignJson(design.stringify());
        TreeDesign savedDesign = treeDesignRepo.save(treeDesign);
        LOGGER.info("New Design Id: " + savedDesign.getId());
        return new ResponseEntity<>(String.valueOf(savedDesign.getId()), HttpStatus.OK);
    }

    @PostMapping("/updateTreeDesign")
    ResponseEntity<String> updateTreeDesign(@RequestBody FamilyTreeDesignJSON design)
    {
        LOGGER.info("Updating tree design No. :" + design.getId());
        TreeDesign treeDesign = treeDesignRepo.findById(design.getId()).orElse(null);
        if (treeDesign != null)
        {
            treeDesign.setDateCreated(LocalDateTime.now().toString());
            treeDesign.setDesignJson(design.stringify());
            treeDesign.setId(design.getId());
            System.out.println(design.stringify());
            TreeDesign savedDesign = treeDesignRepo.save(treeDesign);
            System.out.println(savedDesign.toString());
            LOGGER.info("Saved Design Id: " + savedDesign.getId());
            return new ResponseEntity<>(String.valueOf(savedDesign.getId()), HttpStatus.OK);
        } else
        {
            LOGGER.warn("No Design with the requested Id was found");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }

    @PostMapping("/addTreeOrder")
    ResponseEntity<String> addUser(@RequestBody TreeOrder treeOrder)
    {
        LOGGER.info("Adding new tree order");
        int userId = treeOrder.getUserByUserId().getId();
        LOGGER.info("User id: " + userId);
        User user = userRepo.findById(userId).orElse(null);
        if (user == null) return new ResponseEntity<>(HttpStatus.I_AM_A_TEAPOT);

        int treeDesignId = treeOrder.getTreeDesignById().getId();
        LOGGER.info("Design id: " + treeDesignId);
        TreeDesign treeDesign = treeDesignRepo.findById(treeDesignId).orElse(null);

        if (treeDesign == null) return new ResponseEntity<>(HttpStatus.I_AM_A_TEAPOT);
        TreeOrder savedOrder = treeOrderRepo.save(treeOrder);
        LOGGER.info("New order id: " + savedOrder.getOrderId());
        return new ResponseEntity<>(String.valueOf(savedOrder.getOrderId()), HttpStatus.OK);
    }

    @GetMapping("/getUsers")
    ResponseEntity<List<User>> getUsers()
    {
        LOGGER.info("Getting all users");
        return new ResponseEntity<>(userRepo.findAll(), HttpStatus.OK);
    }

    @GetMapping("/getTreeDesigns")
    ResponseEntity<List<TreeDesign>> getTreeDesigns()
    {
        LOGGER.info("Getting all tree designs");
        return new ResponseEntity<>(treeDesignRepo.findAll(), HttpStatus.OK);
    }

    @GetMapping("/getTreeOrders")
    ResponseEntity<List<TreeOrder>> getTreeOrder(HttpServletRequest request, @RequestParam(required = false, name = "status") String orderStatus)
    {
        LOGGER.info("Getting Tree orders for session " + request.getSession().getId());
        int userId = 0;
        try
        {
            LOGGER.info("Current User Returned body: " + getCurrentUser(request).getBody());
            userId = Integer.parseInt(getCurrentUser(request).getBody());
        } catch (NumberFormatException | NullPointerException e)
        {
            LOGGER.error("Failed to parse userId", e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        List<TreeOrder> orderList = treeOrderRepo.findAllByUserId(userId);
        if (orderStatus != null && !orderStatus.equals(""))
        {
            orderList.removeIf(order -> !order.getStatus().equals(orderStatus));
        }
        return new ResponseEntity<>(orderList, HttpStatus.OK);
    }

    @GetMapping("/getCurrentUser")
    ResponseEntity<String> getCurrentUser(HttpServletRequest request)
    {
        HttpSession session = request.getSession();
        if (session.getAttribute("userId") != null)
        {
            String userId = session.getAttribute("userId").toString();
            LOGGER.info("Current User ID: " + userId);
            return new ResponseEntity<>(userId, HttpStatus.OK);
        }

        User user = userRepo.findOneByEmail(request.getSession().getId());
        if (user == null)
        {
            LOGGER.info("User not found, creating a new one");
            user = new User();
            user.setName("Temp session user");
            user.setEmail(request.getSession().getId());
            user.setVerification("temp");
            LOGGER.info("New user: " + user.toString());
            user = userRepo.save(user);
            LOGGER.info("Saved user: " + user.toString());
        } else
        {
            LOGGER.info("User found: " + user.toString());
        }
        return new ResponseEntity<>(String.valueOf(user.getId()), HttpStatus.OK);
    }

    @ResponseBody
    @GetMapping(value = {"/products/getFamilyTree", "/products/getFamilyTree", "/products/getFamilyTreeDesign"})
    public ResponseEntity<String> generateFamilyTree(@RequestParam Integer designId)
    {
        if (designId == null)
        {
            LOGGER.warn("Bitch the ID is null");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        LOGGER.info("ID :" + designId);
        var design = treeDesignRepo.findById(designId).orElse(null);
        if (design == null)
        {
            LOGGER.warn("The design was null, 400");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        LOGGER.info("Design: " + design.getDesignJson());
        return new ResponseEntity<>(design.getDesignJson(), HttpStatus.OK);
    }

    @GetMapping("/removeTreeOrder/{id}")
    ResponseEntity<String> removeTreeOrder(@PathVariable int id, HttpServletRequest request)
    {
        LOGGER.info("Removing a Tree Order " + id);
        var orderList = getTreeOrder(request, null).getBody();
        if (orderList == null || orderList.size() == 0)
        {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else
        {
            LOGGER.info("Removing a Tree Order with an id of " + id);
            for (TreeOrder order : orderList)
            {
                if (order.getOrderId() == id)
                {
                    LOGGER.info("Order found, changing status to canceled");
                    order.setStatus("cancelled");
                    treeOrderRepo.save(order);
                }
            }
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
