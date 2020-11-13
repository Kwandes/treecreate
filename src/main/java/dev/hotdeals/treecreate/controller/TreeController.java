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
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hibernate.bytecode.BytecodeLogger.LOGGER;

@RestController
public class TreeController
{
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    @Autowired
    UserRepo userRepo;

    @Autowired
    TreeOrderRepo treeOrderRepo;

    @Autowired
    TreeDesignRepo treeDesignRepo;

    @PostMapping("/addTreeDesign")
    ResponseEntity<String> addTreeDesign(@RequestBody FamilyTreeDesignJSON design, HttpServletRequest request)
    {
        LOGGER.info( request.getSession().getId() + " - Adding a new tree design");
        TreeDesign treeDesign = new TreeDesign();
        treeDesign.setDateCreated(LocalDateTime.now().format(formatter).toString());
        treeDesign.setDesignJson(design.stringify());
        TreeDesign savedDesign = treeDesignRepo.save(treeDesign);
        LOGGER.info(request.getSession().getId() + " - New Design Id: " + savedDesign.getId());
        return new ResponseEntity<>(String.valueOf(savedDesign.getId()), HttpStatus.OK);
    }

    @PostMapping("/updateTreeDesign")
    ResponseEntity<String> updateTreeDesign(@RequestBody FamilyTreeDesignJSON design)
    {
        LOGGER.info("Updating tree design No. :" + design.getId());
        TreeDesign treeDesign = treeDesignRepo.findById(design.getId()).orElse(null);
        if (treeDesign != null)
        {
            treeDesign.setDateCreated(LocalDateTime.now().format(formatter));
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
    ResponseEntity<String> addUser(@RequestBody TreeOrder treeOrder, HttpServletRequest request)
    {
        LOGGER.info(request.getSession().getId() + " - Adding new tree order");
        int userId = treeOrder.getUserByUserId().getId();
        LOGGER.info(request.getSession().getId() + " - New tree order - User id: " + userId);
        User user = userRepo.findById(userId).orElse(null);
        if (user == null) return new ResponseEntity<>(HttpStatus.I_AM_A_TEAPOT);

        int treeDesignId = treeOrder.getTreeDesignById().getId();
        LOGGER.info(request.getSession().getId() + " - New tree order - Design id: " + treeDesignId);
        TreeDesign treeDesign = treeDesignRepo.findById(treeDesignId).orElse(null);

        if (treeDesign == null) return new ResponseEntity<>(HttpStatus.I_AM_A_TEAPOT);
        TreeOrder savedOrder = treeOrderRepo.save(treeOrder);
        LOGGER.info(request.getSession().getId() + " - New order id: " + savedOrder.getOrderId());
        return new ResponseEntity<>(String.valueOf(savedOrder.getOrderId()), HttpStatus.OK);
    }

    @PostMapping("/updateTreeOrder")
    ResponseEntity<String> updateTreeOrder(@RequestBody TreeOrder treeOrder, HttpServletRequest request)
    {
        LOGGER.info(request.getSession().getId() + " - Editing tree order");
        int userId = treeOrder.getUserByUserId().getId();
        LOGGER.info(request.getSession().getId() + " - Editing tree order - User id: " + userId);
        User user = userRepo.findById(userId).orElse(null);
        if (user == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        int treeDesignId = treeOrder.getTreeDesignById().getId();
        LOGGER.info(request.getSession().getId() + " - Editing tree order - Design id: " + treeDesignId);
        TreeDesign treeDesign = treeDesignRepo.findById(treeDesignId).orElse(null);
        if (treeDesign == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        TreeOrder order = treeOrderRepo.findByTreeDesignId(treeDesignId).orElse(null);
        if (order != null)
        {
            order.setAmount(treeOrder.getAmount());
            order.setSize(treeOrder.getSize());
            order.setStatus(treeOrder.getStatus());
            order.setTreeDesignById(treeOrder.getTreeDesignById());
            order.setUserByUserId(treeOrder.getUserByUserId());
            TreeOrder savedOrder = treeOrderRepo.save(order);
            System.out.println(savedOrder.toString());
            LOGGER.info(request.getSession().getId() + " - Editing tree order - New order id: " + savedOrder.getOrderId());
            return new ResponseEntity<>(String.valueOf(savedOrder.getOrderId()), HttpStatus.OK);
        } else
        {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

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
        //LOGGER.info("Getting Tree orders for session " + request.getSession().getId());
        int userId = 0;
        try
        {
            //LOGGER.info("Current User Returned body: " + getCurrentUser(request).getBody());
            userId = Integer.parseInt(getCurrentUser(request).getBody());
        } catch (NumberFormatException | NullPointerException e)
        {
            LOGGER.error(request.getSession().getId() + " - Get Tree orders - Failed to parse userId", e);
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
            //LOGGER.info("Current User ID: " + userId);
            return new ResponseEntity<>(userId, HttpStatus.OK);
        }

        User user = userRepo.findOneByEmail(request.getSession().getId());
        if (user == null)
        {
            LOGGER.info(request.getSession().getId() + " - Get current User - User not found, creating a new one");
            user = new User();
            user.setName("Temp session user");
            user.setEmail(request.getSession().getId());
            user.setVerification("temp");
            LOGGER.info(request.getSession().getId() + " - Get current User - New user: " + user.toString());
            user = userRepo.save(user);
            LOGGER.info(request.getSession().getId() + " - Get current User - Saved user: " + user.toString());
        } else
        {
            //LOGGER.info("User found: " + user.toString());
        }
        return new ResponseEntity<>(String.valueOf(user.getId()), HttpStatus.OK);
    }

    @ResponseBody
    @GetMapping(value = {"/products/getFamilyTree", "/products/getFamilyTree", "/products/getFamilyTreeDesign"})
    public ResponseEntity<String> generateFamilyTree(@RequestParam Integer designId, HttpServletRequest request)
    {
        if (designId == null)
        {
            LOGGER.warn(request.getSession().getId() + " - Get Tree Design - Bitch the ID is null");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        LOGGER.info(request.getSession().getId() + " - Get Tree Design - ID :" + designId);
        var design = treeDesignRepo.findById(designId).orElse(null);
        if (design == null)
        {
            LOGGER.warn(request.getSession().getId() + " - Get Tree Design - The design was null, 400");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        LOGGER.info(request.getSession().getId() + " - Get Tree Design - Design: " + design.getDesignJson());
        return new ResponseEntity<>(design.getDesignJson(), HttpStatus.OK);
    }

    @GetMapping("/removeTreeOrder/{id}")
    ResponseEntity<String> removeTreeOrder(@PathVariable int id, HttpServletRequest request)
    {
        LOGGER.info(request.getSession().getId() + " - Removing a Tree Order " + id);
        var orderList = getTreeOrder(request, null).getBody();
        if (orderList == null || orderList.size() == 0)
        {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else
        {
            for (TreeOrder order : orderList)
            {
                if (order.getOrderId() == id)
                {
                    LOGGER.info(request.getSession().getId() + " - Removing a Tree Order - Order found, changing status to canceled");
                    order.setStatus("cancelled");
                    treeOrderRepo.save(order);
                }
            }
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ResponseBody
    @GetMapping(value = {"/products/getTreeOrderByDesignId"})
    public ResponseEntity<String> getTreeOrderByDesignId(@RequestParam Integer designId, HttpServletRequest request)
    {
        if (designId == null)
        {
            LOGGER.warn(request.getSession().getId() + " - Getting tree order by design id - The Design ID is null");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        LOGGER.info(request.getSession().getId() + " - Getting tree order by design id - ID :" + designId);
        var order = treeOrderRepo.findByTreeDesignId(designId).orElse(null);
        if (order == null)
        {
            LOGGER.warn(request.getSession().getId() + " - Getting tree order by design id - The design was null, 400");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        LOGGER.info(request.getSession().getId() + " - Getting tree order by design id - Order: " + order.toString());
        return new ResponseEntity<>(order.stringify(), HttpStatus.OK);
    }
}
