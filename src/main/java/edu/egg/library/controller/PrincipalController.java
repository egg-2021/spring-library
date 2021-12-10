package edu.egg.library.controller;

import edu.egg.library.entity.Usuario;
import edu.egg.library.enums.Rol;
import edu.egg.library.exception.SpringException;
import edu.egg.library.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;
import java.util.Map;

@Controller
public class PrincipalController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/home")
    public ModelAndView inicio() {
        return new ModelAndView("index");
    }

    @GetMapping("/login")
    public ModelAndView login(@RequestParam(required = false) String error, @RequestParam(required = false) String logout, Principal principal) {
        ModelAndView mav = new ModelAndView("login");

        if (error != null) {
            mav.addObject("error", "Correo o contraseña inválida");
        }

        if (logout != null) {
            mav.addObject("logout", "Ha salido correctamente de la plataforma");
        }

        if (principal != null) {
            mav.setViewName("redirect:/home");
        }

        return mav;
    }

    @GetMapping("/signup")
    public ModelAndView signup(HttpServletRequest request, Principal principal) {
        ModelAndView mav = new ModelAndView("signup");
        Map<String, ?> flashMap = RequestContextUtils.getInputFlashMap(request);

        if (principal != null) {
            mav.setViewName("redirect:/home");
        }

        if (flashMap != null) {
            mav.addObject("error", flashMap.get("error"));
            mav.addObject("usuario", flashMap.get("usuario"));
        } else {
            Usuario usuario = new Usuario();
            usuario.setRol(Rol.USER);
            mav.addObject("usuario", usuario);
        }

        return mav;
    }

    @PostMapping("/registro")
    public ModelAndView signup(@Valid @ModelAttribute Usuario usuario, BindingResult result, HttpServletRequest request, RedirectAttributes attributes) {
        ModelAndView mav = new ModelAndView();

        if (result.hasErrors()) {
            mav.addObject("usuario", usuario);
            mav.setViewName("signup");
            return mav;
        }

        try {
            usuarioService.crear(usuario);
            request.login(usuario.getCorreo(), usuario.getClave());
            mav.setViewName("redirect:/home");
        } catch (SpringException e) {
            attributes.addFlashAttribute("usuario", usuario);
            attributes.addFlashAttribute("error", e.getMessage());
            mav.setViewName("redirect:/signup");
        } catch (ServletException e) {
            attributes.addFlashAttribute("error", "Error al realizar auto-login");
        }

        return mav;
    }
}
