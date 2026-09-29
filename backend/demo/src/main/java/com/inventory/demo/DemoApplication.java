package com.inventory.demo;

import com.inventory.demo.model.Product;
import com.inventory.demo.enums.Category;
import com.inventory.demo.enums.ProductLifecycle;
import com.inventory.demo.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Bean
	CommandLineRunner initDatabase(ProductRepository productRepository) {
		return args -> {
			// Clear existing data
			productRepository.deleteAll();

			// Add seed data - 8 products as specified
			Product laptop = new Product();
			laptop.setSku("ELEC-LAPTOP-001");
			laptop.setName("Gaming Laptop Pro");
			laptop.setCategory(Category.ELECTRONICS);
			laptop.setCurrentPrice(new BigDecimal("1299.99"));
			laptop.setStockLevel(15);
			laptop.setReorderThreshold(10);
			laptop.setDemandVelocity(5);
			laptop.setLifecycle(ProductLifecycle.ACTIVE);
			productRepository.save(laptop);

			Product smartphone = new Product();
			smartphone.setSku("ELEC-PHONE-001");
			smartphone.setName("Smartphone X");
			smartphone.setCategory(Category.ELECTRONICS);
			smartphone.setCurrentPrice(new BigDecimal("799.99"));
			smartphone.setStockLevel(25);
			smartphone.setReorderThreshold(20);
			smartphone.setDemandVelocity(12);
			smartphone.setLifecycle(ProductLifecycle.ACTIVE);
			productRepository.save(smartphone);

			Product tshirt = new Product();
			tshirt.setSku("APPAREL-TSHIRT-001");
			tshirt.setName("Cotton T-Shirt");
			tshirt.setCategory(Category.APPAREL);
			tshirt.setCurrentPrice(new BigDecimal("19.99"));
			tshirt.setStockLevel(50);
			tshirt.setReorderThreshold(30);
			tshirt.setDemandVelocity(8);
			tshirt.setLifecycle(ProductLifecycle.ACTIVE);
			productRepository.save(tshirt);

			Product jeans = new Product();
			jeans.setSku("APPAREL-JEANS-001");
			jeans.setName("Slim Fit Jeans");
			jeans.setCategory(Category.APPAREL);
			jeans.setCurrentPrice(new BigDecimal("49.99"));
			jeans.setStockLevel(30);
			jeans.setReorderThreshold(25);
			jeans.setDemandVelocity(3);
			jeans.setLifecycle(ProductLifecycle.ACTIVE);
			productRepository.save(jeans);

			Product blender = new Product();
			blender.setSku("HOME-BLENDER-001");
			blender.setName("Professional Blender");
			blender.setCategory(Category.HOME);
			blender.setCurrentPrice(new BigDecimal("89.99"));
			blender.setStockLevel(12);
			blender.setReorderThreshold(15);
			blender.setDemandVelocity(2);
			blender.setLifecycle(ProductLifecycle.ACTIVE);
			productRepository.save(blender);

			Product pillow = new Product();
			pillow.setSku("HOME-PILLOW-001");
			pillow.setName("Memory Foam Pillow");
			pillow.setCategory(Category.HOME);
			pillow.setCurrentPrice(new BigDecimal("29.99"));
			pillow.setStockLevel(40);
			pillow.setReorderThreshold(20);
			pillow.setDemandVelocity(4);
			pillow.setLifecycle(ProductLifecycle.ACTIVE);
			productRepository.save(pillow);

			// Product near reorder threshold for demo path
			Product headphones = new Product();
			headphones.setSku("ELEC-HEAD-001");
			headphones.setName("Wireless Headphones");
			headphones.setCategory(Category.ELECTRONICS);
			headphones.setCurrentPrice(new BigDecimal("149.99"));
			headphones.setStockLevel(8); // Near reorder threshold of 10
			headphones.setReorderThreshold(10);
			headphones.setDemandVelocity(7);
			headphones.setLifecycle(ProductLifecycle.ACTIVE);
			productRepository.save(headphones);

			Product tablet = new Product();
			tablet.setSku("ELEC-TABLET-001");
			tablet.setName("Tablet 10 inch");
			tablet.setCategory(Category.ELECTRONICS);
			tablet.setCurrentPrice(new BigDecimal("399.99"));
			tablet.setStockLevel(18);
			tablet.setReorderThreshold(15);
			tablet.setDemandVelocity(6);
			tablet.setLifecycle(ProductLifecycle.ACTIVE);
			productRepository.save(tablet);

			System.out.println("Database initialized with seed data!");
		};
	}
}
