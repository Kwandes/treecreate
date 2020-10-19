package dev.hotdeals.treecreate.controller;

import dev.hotdeals.treecreate.model.FamilyTreeDesign;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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

    @ResponseBody
    @PostMapping("/saveFamilyTreeDesign")
    public ResponseEntity<Boolean> saveFamilyTreeDesign(@RequestBody FamilyTreeDesign design)
    {
        LOGGER.info("Design received: " + design);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
