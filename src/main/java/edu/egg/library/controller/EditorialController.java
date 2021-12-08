package edu.egg.library.controller;

import edu.egg.library.entity.Editorial;
import edu.egg.library.exception.SpringException;
import edu.egg.library.service.EditorialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Map;

@Controller
@RequestMapping("/editorial")
public class EditorialController {

    @Autowired
    private EditorialService editorialService;

    @GetMapping
    public ModelAndView mostrar(HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("editoriales");
        Map<String, ?> flashMap = RequestContextUtils.getInputFlashMap(request);

        if (flashMap != null) {
            mav.addObject("exito", flashMap.get("exito"));
            mav.addObject("error", flashMap.get("error"));
        }

        mav.addObject("editoriales", editorialService.buscarTodos());
        return mav;
    }

    @GetMapping("/crear")
    public ModelAndView crear(HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("editorial-formulario");
        Map<String, ?> flashMap = RequestContextUtils.getInputFlashMap(request);

        if (flashMap != null) {
            mav.addObject("error", flashMap.get("error"));
            mav.addObject("editorial", flashMap.get("editorial"));
        } else {
            mav.addObject("editorial", new Editorial());
        }

        mav.addObject("title", "Crear Editorial");
        mav.addObject("action", "guardar");
        return mav;
    }

    @GetMapping("/editar/{id}")
    public ModelAndView editar(@PathVariable Integer id, HttpServletRequest request, RedirectAttributes attributes) {
        ModelAndView mav = new ModelAndView("editorial-formulario");
        Map<String, ?> flashMap = RequestContextUtils.getInputFlashMap(request);

        try {
            if (flashMap != null) {
                mav.addObject("error", flashMap.get("error"));
                mav.addObject("editorial", flashMap.get("editorial"));
            } else {
                mav.addObject("editorial", editorialService.buscarPorId(id));
            }

            mav.addObject("title", "Editar Editorial");
            mav.addObject("action", "modificar");
        } catch (SpringException e) {
            attributes.addFlashAttribute("error", e.getMessage());
            mav.setViewName("redirect:/editorial");
        }

        return mav;
    }

    @PostMapping("/guardar")
    public ModelAndView guardar(@Valid @ModelAttribute Editorial editorial, BindingResult result, RedirectAttributes attributes) {
        ModelAndView mav = new ModelAndView();

        if (result.hasErrors()) {
            mav.addObject("title", "Crear Editorial");
            mav.addObject("action", "guardar");
            mav.addObject("editorial", editorial);
            mav.setViewName("editorial-formulario");
            return mav;
        }

        try {
            editorialService.crear(editorial);
            attributes.addFlashAttribute("exito", "La creación ha sido realizada satisfactoriamente");
            mav.setViewName("redirect:/editorial");
        } catch (SpringException e) {
            attributes.addFlashAttribute("editorial", editorial);
            attributes.addFlashAttribute("error", e.getMessage());
            mav.setViewName("redirect:/editorial/crear");
        }

        return mav;
    }

    @PostMapping("/modificar")
    public ModelAndView modificar(@Valid @ModelAttribute Editorial editorial, BindingResult result, RedirectAttributes attributes) {
        ModelAndView mav = new ModelAndView();

        if (result.hasErrors()) {
            mav.addObject("title", "Editar Editorial");
            mav.addObject("action", "modificar");
            mav.addObject("editorial", editorial);
            mav.setViewName("editorial-formulario");
            return mav;
        }

        try {
            editorialService.modificar(editorial);
            attributes.addFlashAttribute("exito", "La actualización ha sido realizada satisfactoriamente");
            mav.setViewName("redirect:/editorial");
        } catch (SpringException e) {
            attributes.addFlashAttribute("editorial", editorial);
            attributes.addFlashAttribute("error", e.getMessage());
            mav.setViewName("redirect:/editorial/editar/" + editorial.getId());
        }

        return mav;
    }

    @PostMapping("/habilitar/{id}")
    public RedirectView habilitar(@PathVariable Integer id) {
        editorialService.habilitar(id);
        return new RedirectView("/editorial");
    }

    @PostMapping("/eliminar/{id}")
    public RedirectView eliminar(@PathVariable Integer id) {
        editorialService.eliminar(id);
        return new RedirectView("/editorial");
    }
}
