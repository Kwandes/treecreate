package dev.hotdeals.treecreate.controller;

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
    ResponseEntity addTreeDesign(@RequestBody TreeDesign treeDesign)
    {
        System.out.println("Adding a new tree design");
        treeDesignRepo.save(treeDesign);
        return new ResponseEntity(HttpStatus.OK);
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
        treeOrderRepo.save(treeOrder);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/getUsers")
    ResponseEntity<List<User>> getusers()
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
}
