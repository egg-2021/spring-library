package edu.egg.library.controller;

import edu.egg.library.entity.Libro;
import edu.egg.library.exception.SpringException;
import edu.egg.library.service.AutorService;
import edu.egg.library.service.EditorialService;
import edu.egg.library.service.LibroService;
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
@RequestMapping("/libro")
public class LibroController {

    @Autowired
    private LibroService libroService;

    @Autowired
    private AutorService autorService;

    @Autowired
    private EditorialService editorialService;

    @GetMapping
    public ModelAndView mostrar(HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("libros");
        Map<String, ?> flashMap = RequestContextUtils.getInputFlashMap(request);

        if (flashMap != null) {
            mav.addObject("exito", flashMap.get("exito"));
            mav.addObject("error", flashMap.get("error"));
        }

        mav.addObject("libros", libroService.buscarTodos());
        return mav;
    }

    @GetMapping("/crear")
    public ModelAndView crear(HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("libro-formulario");
        Map<String, ?> flashMap = RequestContextUtils.getInputFlashMap(request);

        if (flashMap != null) {
            mav.addObject("error", flashMap.get("error"));
            mav.addObject("libro", flashMap.get("libro"));
        } else {
            mav.addObject("libro", new Libro());
        }

        mav.addObject("autores", autorService.buscarTodos());
        mav.addObject("editoriales", editorialService.buscarTodos());
        mav.addObject("title", "Crear Libro");
        mav.addObject("action", "guardar");
        return mav;
    }

    @GetMapping("/editar/{id}")
    public ModelAndView editar(@PathVariable Integer id, HttpServletRequest request, RedirectAttributes attributes) {
        ModelAndView mav = new ModelAndView("libro-formulario");
        Map<String, ?> flashMap = RequestContextUtils.getInputFlashMap(request);

        try {
            if (flashMap != null) {
                mav.addObject("error", flashMap.get("error"));
                mav.addObject("libro", flashMap.get("libro"));
            } else {
                mav.addObject("libro", libroService.buscarPorId(id));
            }

            mav.addObject("autores", autorService.buscarTodos());
            mav.addObject("editoriales", editorialService.buscarTodos());
            mav.addObject("title", "Editar Libro");
            mav.addObject("action", "modificar");
        } catch (SpringException e) {
            attributes.addFlashAttribute("error", e.getMessage());
            mav.setViewName("redirect:/libro");
        }

        return mav;
    }

    @PostMapping("/guardar")
    public ModelAndView guardar(@Valid @ModelAttribute Libro libro, BindingResult result, RedirectAttributes attributes) {
        ModelAndView mav = new ModelAndView();

        if (result.hasErrors()) {
            mav.addObject("autores", autorService.buscarTodos());
            mav.addObject("editoriales", editorialService.buscarTodos());
            mav.addObject("title", "Crear Libro");
            mav.addObject("action", "guardar");
            mav.addObject("libro", libro);
            mav.setViewName("libro-formulario");
            return mav;
        }

        try {
            libroService.crear(libro);
            attributes.addFlashAttribute("exito", "La creación ha sido realizada satisfactoriamente");
            mav.setViewName("redirect:/libro");
        } catch (SpringException e) {
            attributes.addFlashAttribute("libro", libro);
            attributes.addFlashAttribute("error", e.getMessage());
            mav.setViewName("redirect:/libro/crear");
        }

        return mav;
    }

    @PostMapping("/modificar")
    public ModelAndView modificar(@Valid @ModelAttribute Libro libro, BindingResult result, RedirectAttributes attributes) {
        ModelAndView mav = new ModelAndView();

        if (result.hasErrors()) {
            mav.addObject("autores", autorService.buscarTodos());
            mav.addObject("editoriales", editorialService.buscarTodos());
            mav.addObject("title", "Editar Libro");
            mav.addObject("action", "modificar");
            mav.addObject("libro", libro);
            mav.setViewName("libro-formulario");
            return mav;
        }

        try {
            libroService.modificar(libro);
            attributes.addFlashAttribute("exito", "La actualización ha sido realizada satisfactoriamente");
            mav.setViewName("redirect:/libro");
        } catch (SpringException e) {
            attributes.addFlashAttribute("libro", libro);
            attributes.addFlashAttribute("error", e.getMessage());
            mav.setViewName("redirect:/libro/editar/" + libro.getId());
        }

        return mav;
    }

    @PostMapping("/habilitar/{id}")
    public RedirectView habilitar(@PathVariable Integer id) {
        libroService.habilitar(id);
        return new RedirectView("/libro");
    }

    @PostMapping("/eliminar/{id}")
    public RedirectView eliminar(@PathVariable Integer id) {
        libroService.eliminar(id);
        return new RedirectView("/libro");
    }
}
