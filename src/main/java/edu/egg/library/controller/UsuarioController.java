package edu.egg.library.controller;

import edu.egg.library.entity.Usuario;
import edu.egg.library.exception.SpringException;
import edu.egg.library.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.Map;

@Controller
// @PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/usuario")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public ModelAndView mostrar(HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("usuarios");
        Map<String, ?> flashMap = RequestContextUtils.getInputFlashMap(request);

        if (flashMap != null) {
            mav.addObject("exito", flashMap.get("exito"));
            mav.addObject("error", flashMap.get("error"));
        }

        mav.addObject("usuarios", usuarioService.buscarTodos());
        return mav;
    }

    @GetMapping("/crear")
    public ModelAndView crear(HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("usuario-formulario");
        Map<String, ?> flashMap = RequestContextUtils.getInputFlashMap(request);

        if (flashMap != null) {
            mav.addObject("error", flashMap.get("error"));
            mav.addObject("usuario", flashMap.get("usuario"));
        } else {
            mav.addObject("usuario", new Usuario());
        }

        mav.addObject("title", "Crear Usuario");
        mav.addObject("action", "guardar");
        return mav;
    }

    @GetMapping("/editar/{id}")
    public ModelAndView editar(@PathVariable Integer id, HttpServletRequest request, HttpSession session, RedirectAttributes attributes) {
        if (!session.getAttribute("id").equals(id)) {
            return new ModelAndView(new RedirectView("/home"));
        }

        ModelAndView mav = new ModelAndView("usuario-formulario");
        Map<String, ?> flashMap = RequestContextUtils.getInputFlashMap(request);

        try {
            if (flashMap != null) {
                mav.addObject("error", flashMap.get("error"));
                mav.addObject("usuario", flashMap.get("usuario"));
            } else {
                mav.addObject("usuario", usuarioService.buscarPorId(id));
            }

            mav.addObject("title", "Editar Usuario");
            mav.addObject("action", "modificar");
        } catch (SpringException e) {
            attributes.addFlashAttribute("error", e.getMessage());
            mav.setViewName("redirect:/usuario");
        }

        return mav;
    }

    @PostMapping("/guardar")
    public ModelAndView guardar(@Valid @ModelAttribute Usuario usuario, BindingResult result, RedirectAttributes attributes) {
        ModelAndView mav = new ModelAndView();

        if (result.hasErrors()) {
            mav.addObject("title", "Crear Usuario");
            mav.addObject("action", "guardar");
            mav.addObject("usuario", usuario);
            mav.setViewName("usuario-formulario");
            return mav;
        }

        try {
            usuarioService.crear(usuario);
            attributes.addFlashAttribute("exito", "La creación ha sido realizada satisfactoriamente");
            mav.setViewName("redirect:/usuario");
        } catch (SpringException e) {
            attributes.addFlashAttribute("usuario", usuario);
            attributes.addFlashAttribute("error", e.getMessage());
            mav.setViewName("redirect:/usuario/crear");
        }

        return mav;
    }

    @PostMapping("/modificar")
    public ModelAndView modificar(@Valid @ModelAttribute Usuario usuario, BindingResult result, RedirectAttributes attributes) {
        ModelAndView mav = new ModelAndView();

        if (result.hasErrors()) {
            mav.addObject("title", "Editar Usuario");
            mav.addObject("action", "modificar");
            mav.addObject("usuario", usuario);
            mav.setViewName("usuario-formulario");
            return mav;
        }

        try {
            usuarioService.modificar(usuario);
            attributes.addFlashAttribute("exito", "La actualización ha sido realizada satisfactoriamente");
            mav.setViewName("redirect:/usuario");
        } catch (SpringException e) {
            attributes.addFlashAttribute("usuario", usuario);
            attributes.addFlashAttribute("error", e.getMessage());
            mav.setViewName("redirect:/usuario/editar/" + usuario.getId());
        }

        return mav;
    }

    @PostMapping("/habilitar/{id}")
    public RedirectView habilitar(@PathVariable Integer id) {
        usuarioService.habilitar(id);
        return new RedirectView("/usuario");
    }

    @PostMapping("/eliminar/{id}")
    public RedirectView eliminar(@PathVariable Integer id) {
        usuarioService.eliminar(id);
        return new RedirectView("/usuario");
    }
}
