package dev.hotdeals.treecreate.controller;

import dev.hotdeals.treecreate.model.FamilyTree;
import dev.hotdeals.treecreate.model.FamilyTreeDesignJSON;
import dev.hotdeals.treecreate.repository.FamilyTreeRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

import static org.hibernate.bytecode.BytecodeLogger.LOGGER;

@Controller
public class ProductController
{
    // indexing mapping for the products section. It determines which page to display
    @RequestMapping(value = {"/products"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String productIndex()
    {
        return "redirect:/products/familyTree";
    }

    @GetMapping("/products/familyTree")
    public String familyTree()
    {
        return "products/familyTree";
    }

    @GetMapping("/products/testProduct")
    public String testProduct()
    {
        return "products/testProduct";
    }

    @GetMapping("/products/template")
    public String productTemplate()
    {
        return "products/productTemplate";
    }

    @Autowired
    FamilyTreeRepo familyTreeRepo;

    @ResponseBody
    @PostMapping("/saveFamilyTreeDesign")
    public ResponseEntity<Boolean> saveFamilyTreeDesign(@RequestBody FamilyTreeDesignJSON design, HttpServletRequest request)
    {
        LOGGER.info("Design received: " + design);
        var familyTree = new FamilyTree();
        familyTree.setDesign(design);
        familyTree.setTimePLusDate(LocalDateTime.now().toString());
        if (request.getSession().getAttribute("userId") != null)
        {
            familyTree.setOwnerId(request.getSession().getAttribute("userId").toString());
        }
        familyTreeRepo.save(familyTree);
        LOGGER.info("Added a new design: " + familyTree);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
