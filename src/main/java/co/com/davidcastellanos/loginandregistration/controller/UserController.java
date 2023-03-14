package co.com.davidcastellanos.loginandregistration.controller;

import co.com.davidcastellanos.loginandregistration.model.LoginUser;
import co.com.davidcastellanos.loginandregistration.model.User;
import co.com.davidcastellanos.loginandregistration.service.AppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
public class UserController {
    @Autowired
    private AppService service;

    @GetMapping("/")
    public String index(@ModelAttribute("nuevoUser") User nuevoUser,
                        @ModelAttribute("nuevoLogin") LoginUser loginUser) {
        return "index.jsp";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("nuevoUser") User nuevoUser,
                               BindingResult result,
                               Model model,
                               HttpSession session) {
        service.registerUser(nuevoUser, result);

        if (result.hasErrors()) {
            model.addAttribute("nuevoLogin", new LoginUser());
            return "index.jsp";
        } else {
            session.setAttribute("userSession", nuevoUser);
            return "redirect:/dashboard";
        }

    }

    @GetMapping("/dashboard")
    public String dashBoard(HttpSession session) {
        User currentUser = (User) session.getAttribute("userSession");

        if (currentUser == null) {
            return "redirect:/";
        }

        return "dashboard.jsp";
    }

    @PostMapping("/login")
    public String loginUser(@Valid @ModelAttribute("nuevoLogin") LoginUser nuevoLogin,
                            BindingResult result,
                            Model model,
                            HttpSession session) {

        User user = service.loginUser(nuevoLogin, result);
        if (result.hasErrors()) {
            model.addAttribute("nuevoUser", new User());
            return "index.jsp";
        }

        session.setAttribute("userSession", user);
        return "redirect:/dashboard";
    }

    @GetMapping("/logout")
    public String logOutUser(HttpSession session) {
        User currentUser = (User) session.getAttribute("userSession");

        if (currentUser == null) {
            return "redirect:/";
        } else {
            session.removeAttribute("userSession");
            return "redirect:/";
        }
    }

}
