package edu.egg.library.controller;

import edu.egg.library.entity.Autor;
import edu.egg.library.exception.SpringException;
import edu.egg.library.service.AutorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Map;

@Controller
@RequestMapping("/autor")
public class AutorController {

    @Autowired
    private AutorService autorService;

    @GetMapping
    public ModelAndView mostrar(HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("autores");
        Map<String, ?> flashMap = RequestContextUtils.getInputFlashMap(request);

        if (flashMap != null) {
            mav.addObject("exito", flashMap.get("exito"));
            mav.addObject("error", flashMap.get("error"));
        }

        mav.addObject("autores", autorService.buscarTodos());
        return mav;
    }

    @GetMapping("/crear")
    public ModelAndView crear(HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("autor-formulario");
        Map<String, ?> flashMap = RequestContextUtils.getInputFlashMap(request);

        if (flashMap != null) {
            mav.addObject("error", flashMap.get("error"));
            mav.addObject("autor", flashMap.get("autor"));
        } else {
            mav.addObject("autor", new Autor());
        }

        mav.addObject("title", "Crear Autor");
        mav.addObject("action", "guardar");
        return mav;
    }

    @GetMapping("/editar/{id}")
    public ModelAndView editar(@PathVariable Integer id, HttpServletRequest request, RedirectAttributes attributes) {
        ModelAndView mav = new ModelAndView("autor-formulario");
        Map<String, ?> flashMap = RequestContextUtils.getInputFlashMap(request);

        try {
            if (flashMap != null) {
                mav.addObject("error", flashMap.get("error"));
                mav.addObject("autor", flashMap.get("autor"));
            } else {
                mav.addObject("autor", autorService.buscarPorId(id));
            }

            mav.addObject("title", "Editar Autor");
            mav.addObject("action", "modificar");
        } catch (SpringException e) {
            attributes.addFlashAttribute("error", e.getMessage());
            mav.setViewName("redirect:/autor");
        }

        return mav;
    }

    @PostMapping("/guardar")
    public ModelAndView guardar(@RequestParam MultipartFile foto, @Valid @ModelAttribute Autor autor, BindingResult result, RedirectAttributes attributes) {
        ModelAndView mav = new ModelAndView();

        if (result.hasErrors()) {
            mav.addObject("title", "Crear Autor");
            mav.addObject("action", "guardar");
            mav.addObject("autor", autor);
            mav.setViewName("autor-formulario");
            return mav;
        }

        try {
            autorService.crear(autor, foto);
            attributes.addFlashAttribute("exito", "La creación ha sido realizada satisfactoriamente");
            mav.setViewName("redirect:/autor");
        } catch (SpringException e) {
            attributes.addFlashAttribute("autor", autor);
            attributes.addFlashAttribute("error", e.getMessage());
            mav.setViewName("redirect:/autor/crear");
        }

        return mav;
    }

    @PostMapping("/modificar")
    public ModelAndView modificar(@RequestParam MultipartFile foto, @Valid @ModelAttribute Autor autor, BindingResult result, RedirectAttributes attributes) {
        ModelAndView mav = new ModelAndView();

        if (result.hasErrors()) {
            mav.addObject("title", "Editar Autor");
            mav.addObject("action", "modificar");
            mav.addObject("autor", autor);
            mav.setViewName("autor-formulario");
            return mav;
        }

        try {
            autorService.modificar(autor, foto);
            attributes.addFlashAttribute("exito", "La actualización ha sido realizada satisfactoriamente");
            mav.setViewName("redirect:/autor");
        } catch (SpringException e) {
            attributes.addFlashAttribute("autor", autor);
            attributes.addFlashAttribute("error", e.getMessage());
            mav.setViewName("redirect:/autor/editar/" + autor.getId());
        }

        return mav;
    }

    @PostMapping("/habilitar/{id}")
    public RedirectView habilitar(@PathVariable Integer id) {
        autorService.habilitar(id);
        return new RedirectView("/autor");
    }

    @PostMapping("/eliminar/{id}")
    public RedirectView eliminar(@PathVariable Integer id) {
        autorService.eliminar(id);
        return new RedirectView("/autor");
    }
}
