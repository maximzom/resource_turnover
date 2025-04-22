package com.agriculture.resource_turnover.controllers;

import com.agriculture.resource_turnover.models.Resource;
import com.agriculture.resource_turnover.services.ResourceService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/resources")
public class ResourceController {
    private final ResourceService resourceService;

    public ResourceController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @GetMapping
    public String listResources(Model model) {
        model.addAttribute("resources", resourceService.findAll());
        return "resource_list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("resource", new Resource());
        return "resource_form";
    }

    @PostMapping
    public String createResource(@ModelAttribute Resource resource) {
        resourceService.save(resource);
        return "redirect:/resources";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("resource", resourceService.findById(id));
        return "resource_form";
    }

    @PostMapping("/{id}")
    public String updateResource(@PathVariable Long id, @ModelAttribute Resource resource) {
        resource.setId(id);
        resourceService.save(resource);
        return "redirect:/resources";
    }

    @PostMapping("/{id}/archive")
    public String archiveResource(@PathVariable Long id) {
        resourceService.archiveResource(id);
        return "redirect:/resources";
    }
}