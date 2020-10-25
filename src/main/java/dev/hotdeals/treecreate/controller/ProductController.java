package dev.hotdeals.treecreate.controller;

import dev.hotdeals.treecreate.model.FamilyTree;


import dev.hotdeals.treecreate.model.FamilyTreeDesignJSON;
import dev.hotdeals.treecreate.repository.FamilyTreeRepo;

import org.hibernate.internal.build.AllowSysOut;
import org.openqa.selenium.remote.ScreenshotException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;	
import org.springframework.web.multipart.MultipartFile;


import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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
        familyTree.setDesign(design.stringify());
        familyTree.setTimePLusDate(LocalDateTime.now().toString());
        if (request.getSession().getAttribute("userId") != null)
        {
            familyTree.setOwnerId(request.getSession().getAttribute("userId").toString());
        }
        familyTreeRepo.save(familyTree);
        LOGGER.info("Added a new design: " + familyTree);
        LOGGER.info("Stringified version: " + design.stringify());

        
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(value = {"/products/generateFamilyTree", "/products/generate"})
    public String generateFamilyTree()
    {
        return "products/generateFamilyTree";
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
        System.out.println("ID :" + designId);
        var design = familyTreeRepo.findById(designId).orElse(null);
        if (design == null)
        {
            LOGGER.warn("The design was null, 400");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        LOGGER.info("Design: " + design.getDesign());


        return new ResponseEntity<>(design.getDesign(), HttpStatus.OK);
    }
    
    @ResponseBody
    @RequestMapping(path = "/products/familyTree/saveScreenshot", method = RequestMethod.POST)
    public void saveScreenshot(@RequestBody MultipartFile file)
    {
    	System.out.println(file);
    	
    	try {
    		Path dirLocation = Paths.get("/").toAbsolutePath().normalize();
            String fileName = file.getOriginalFilename();
            
            Path dfile = dirLocation.resolve(fileName);
            Files.copy(file.getInputStream(), dfile,StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Jan SUXX: " + fileName);;
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    	
    }
    	
    	
    	
    	
    	/*
    }
    	//System.out.println("pre File 'creation'");
    	File pngImage = new File("screenshotException.png");
    	System.out.println("post File 'creation?'");
    	
    	BufferedImage image = (BufferedImage) imageQuestionmark; // your image
    	OutputStream stream;
		try
		{
			System.out.println("WE GOT IN THE MAIN TRY CATCH");
			stream = new FileOutputStream(pngImage);
			ImageIO.write(image, "png", stream);
		} catch (FileNotFoundException e1)	
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
			System.out.println("OH OH WE GOT A FUCKY WUKY");
		} catch (IOException e)
		{
			e.printStackTrace();
		}

    	
    	return new ResponseEntity<>(HttpStatus.I_AM_A_TEAPOT);
    	
    }
    
    
          
		try {

        File file = imageService.getImage(imageId);

        Path path = Paths.get(file.getAbsolutePath());
        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

        return ResponseEntity.ok().contentLength(file.length()).contentType(MediaType.IMAGE_JPEG).body(resource);
	    } catch (Exception e) {
	        throw new InternalServerException("Unable to generate image");
	    }
     
     */
    
}
