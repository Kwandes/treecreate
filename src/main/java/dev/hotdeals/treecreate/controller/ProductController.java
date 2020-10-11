package dev.hotdeals.treecreate.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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

}
