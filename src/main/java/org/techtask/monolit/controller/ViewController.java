package org.techtask.monolit.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequiredArgsConstructor
public class ViewController {

    @GetMapping("/")
    public ModelAndView mainView() {
        String name = "Рецепты";
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("pageTitle", name);
        modelAndView.setViewName("recipes");
        return modelAndView;
    }

    @GetMapping("/additional")
    public ModelAndView recipeFormView() {
        String name = "Игредиенты";
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("pageTitle", name);
        modelAndView.setViewName("ingredients");
        return modelAndView;
    }
}

