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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;

@RestController
public class TreeController
{
    @Autowired
    UserRepo userRepo;

    @Autowired
    TreeOrderRepo treeOrderRepo;

    @Autowired
    TreeDesignRepo treeDesignRepo;

    @PostMapping("/addUser")
    ResponseEntity addUser(@RequestBody User user)
    {
        System.out.println("Adding new user");
        userRepo.save(user);
        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping("/addTreeDesign")
    ResponseEntity<String> addTreeDesign(@RequestBody FamilyTreeDesignJSON design)
    {
        System.out.println("Adding a new tree design");
        TreeDesign treeDesign = new TreeDesign();
        treeDesign.setDateCreated(LocalDateTime.now().toString());
        treeDesign.setDesignJson(design.stringify());
        TreeDesign savedDesign = treeDesignRepo.save(treeDesign);
        System.out.println("New Design Id: " + savedDesign.getId());
        return new ResponseEntity<>(String.valueOf(savedDesign.getId()), HttpStatus.OK);
    }

    @PostMapping("/addTreeOrder")
    ResponseEntity<String> addUser(@RequestBody TreeOrder treeOrder)
    {
        System.out.println("Adding new tree order");
        int userId = treeOrder.getUserByUserId().getId();
        System.out.println("User id: " + userId);
        User user = userRepo.findById(userId).orElse(null);
        if (user == null) return new ResponseEntity<>(HttpStatus.I_AM_A_TEAPOT);

        int treeDesignId = treeOrder.getTreeDesignById().getId();
        System.out.println("Design id: " + treeDesignId);
        TreeDesign treeDesign = treeDesignRepo.findById(treeDesignId).orElse(null);

        if (treeDesign == null) return new ResponseEntity<>(HttpStatus.I_AM_A_TEAPOT);
        TreeOrder savedOrder = treeOrderRepo.save(treeOrder);
        System.out.println("New order id: " + savedOrder.getOrderId());
        return new ResponseEntity<>(String.valueOf(savedOrder.getOrderId()), HttpStatus.OK);
    }

    @GetMapping("/getUsers")
    ResponseEntity<List<User>> getUsers()
    {
        System.out.println("Getting all users");
        List<User> userList = userRepo.findAll();
        return new ResponseEntity<>(userList, HttpStatus.OK);
    }

    @GetMapping("/getTreeDesigns")
    ResponseEntity<List<TreeDesign>> getTreeDesigns()
    {
        System.out.println("Getting all tree designs");
        List<TreeDesign> treeDesignList = treeDesignRepo.findAll();
        return new ResponseEntity<>(treeDesignList, HttpStatus.OK);
    }

    @GetMapping("/getTreeOrders")
    ResponseEntity<List<TreeOrder>> getTreeOrder()
    {
        System.out.println("Getting all tree orders");
        List<TreeOrder> orderList = treeOrderRepo.findAll();
        return new ResponseEntity<>(orderList, HttpStatus.OK);
    }

    @GetMapping("/getCurrentUser")
    ResponseEntity<String> getCurrentUser(HttpServletRequest request)
    {
        HttpSession session = request.getSession();
        if (session.getAttribute("userId") == null)
        {
            User user = userRepo.findOneByEmail(request.getSession().getId());
            if (user == null)
            {
                System.out.println("User not found, creating a new one");
                user = new User();
                user.setName("Temp session user");
                user.setEmail(request.getSession().getId());
                System.out.println("New user: " + user.toString());
                user = userRepo.save(user);
                System.out.println("Saved user: " + user.toString());
                return new ResponseEntity<>(String.valueOf(user.getId()), HttpStatus.OK);
            } else
            {
                System.out.println("User found: " + user.toString());
                return new ResponseEntity<>(String.valueOf(user.getId()), HttpStatus.OK);
            }
        }
        String userId = session.getAttribute("userId").toString();
        System.out.println("User ID: " + userId);
        return new ResponseEntity<>(userId, HttpStatus.OK);
    }
}
