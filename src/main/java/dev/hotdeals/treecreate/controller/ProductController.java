package dev.hotdeals.treecreate.controller;

import dev.hotdeals.treecreate.model.FamilyTree;
import dev.hotdeals.treecreate.model.FamilyTreeDesignJSON;
import dev.hotdeals.treecreate.repository.FamilyTreeRepo;
import dev.hotdeals.treecreate.repository.TreeDesignRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
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

    @GetMapping(value = {"/products/generateFamilyTree", "/products/generate"})
    public String generateFamilyTree()
    {
        return "products/generateFamilyTree";
    }

    @GetMapping(value = {"/products/readFamilyTree", "/products/read", "/products/readOnlyFamilyTree"})
    public String readFamilyTree()
    {
        return "products/readOnlyFamilyTree";
    }
}
