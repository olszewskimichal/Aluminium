package com.zespolowka.controller;

import com.zespolowka.entity.user.CurrentUser;
import com.zespolowka.entity.user.Role;
import com.zespolowka.entity.user.User;
import com.zespolowka.forms.UserEditForm;
import com.zespolowka.repository.SolutionTestRepository;
import com.zespolowka.service.NotificationService;
import com.zespolowka.service.UserService;
import com.zespolowka.validators.ChangePasswordValidator;
import com.zespolowka.validators.UserEditValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * Created by Pitek on 2015-12-01.
 */
@Controller
@RequestMapping(value = "/user")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService UserService;

    @Autowired
    private final NotificationService notificationService;

    @Autowired
    private final ChangePasswordValidator changePasswordValidator;

    private final UserEditValidator userEditValidator;

    private final SolutionTestRepository solutionTestRepository;


    @Autowired
    public UserController(final UserService UserService, NotificationService notificationService, ChangePasswordValidator changePasswordValidator, UserEditValidator userEditValidator, SolutionTestRepository solutionTestRepository) {
        this.UserService = UserService;
        this.notificationService = notificationService;
        this.changePasswordValidator = changePasswordValidator;
        this.userEditValidator = userEditValidator;
        this.solutionTestRepository = solutionTestRepository;
    }

    @PreAuthorize("@currentUserServiceImpl.canAccessUser(principal, #id)")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String showUserDetail(@PathVariable final Long id, final Model model) {
        logger.info("nazwa metody = showUserDetail");
        try {
            final User user = UserService.getUserById(id)
                    .orElseThrow(
                            () -> new NoSuchElementException(String.format("Uzytkownik o id =%s nie istnieje", id)));
            model.addAttribute(user);
        } catch (final Exception e) {
            logger.error(e.getMessage(), e);
            logger.info("{}" + '\n' + "{}", id.toString(), model);
        }
        return "userDetail";
    }

    @RequestMapping(value = "/show", method = RequestMethod.GET)
    public String showCurrentUserDetail(final Model model) {
        logger.info("nazwa metody = showCurrentUserDetail");
        try {
            final CurrentUser currentUser = (CurrentUser) SecurityContextHolder.getContext()
                    .getAuthentication()
                    .getPrincipal();
            final User user = currentUser.getUser();
            model.addAttribute(user);
            model.addAttribute("Notifications",
                    notificationService.findTop5ByUserIdOrUserRoleOrderByDateDesc(user.getId(), user.getRole()));
        } catch (final RuntimeException e) {
            logger.error(e.getMessage(), e);
        }
        return "userDetail";
    }


    @RequestMapping(value = "/edit", method = RequestMethod.GET)
    public String editCurrentUserDetail(final Model model) {
        logger.info("nazwa metody = showCurrentUserDetail");
        try {
            final CurrentUser currentUser = (CurrentUser) SecurityContextHolder.getContext()
                    .getAuthentication()
                    .getPrincipal();
            final User user = currentUser.getUser();
            model.addAttribute("userEditForm", new UserEditForm(user));
        } catch (final RuntimeException e) {
            logger.error(e.getMessage(), e);
        }
        return "userEdit";
    }

    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public String saveCurrentUser(@ModelAttribute @Valid final UserEditForm userEditForm, final Errors errors, final Model model) {
        logger.info("nazwa metody = saveCurrentUser");
        changePasswordValidator.validate(userEditForm, errors);
        userEditValidator.validate(userEditForm, errors);
        if (errors.hasErrors()) {
            String err = errors.getAllErrors().get(0).toString();
            logger.info("err:{}", err);
            return "userEdit";
        } else {
            final User user = UserService.editUser(userEditForm);
            CurrentUser currentUser = new CurrentUser(user);
            Authentication authentication = new UsernamePasswordAuthenticationToken(currentUser,
                    currentUser.getPassword(), currentUser.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            model.addAttribute("sukces", true);
            return "userEdit";
        }
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SUPERADMIN')")
    @RequestMapping(value = "/edit/{id}", method = RequestMethod.GET)
    public String editUser(@PathVariable final Integer id, final Model model) {
        logger.debug("nazwa metody = editUser");
        try {
            model.addAttribute("userEditForm", new UserEditForm(UserService.getUserById(id)
                    .orElseThrow(
                            () -> new NoSuchElementException(String.format("Uzytkownik o id =%s nie istnieje", id)))));
        } catch (final Exception e) {
            logger.error(e.getMessage(), e);
            logger.info("{}" + '\n' + "{}" + '\n' + "{}", id.toString(), model, UserService.getUserById(id));
        }
        return "userEdit";
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SUPERADMIN')")
    @RequestMapping(value = "/edit/{id}", method = RequestMethod.POST)
    public String saveUser(@PathVariable final Integer id, @ModelAttribute @Valid final UserEditForm userEditForm,
                           final Errors errors) {
        logger.info("nazwa metody = saveUser");
        if (errors.hasErrors()) {
            return "userEdit";
        } else {
            final User user = UserService.editUser(userEditForm);
            return "redirect:/user/" + user.getId();
        }

    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SUPERADMIN')")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    public String deleteUser(@PathVariable final Long id, RedirectAttributes redirectAttributes) {
        final CurrentUser currentUser = (CurrentUser) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        logger.info("nazwa metody = deleteUser");
        logger.info("userDelete{}", id);
        if (Objects.equals(currentUser.getId(), id)) {
            logger.info("Nie mozesz usunac siebie");
            redirectAttributes.addFlashAttribute("blad", true);
            redirectAttributes.addFlashAttribute("message", "Nie mozesz usunac siebie");
        } else {
            User user = UserService.getUserById(id)
                    .orElseThrow(
                            () -> new NoSuchElementException(String.format("Uzytkownik o id =%s nie istnieje", id)));
            if (currentUser.getRole().name().equals("ADMIN") && user.getRole().name().equals("SUPERADMIN")) {
                logger.info("Nie mozesz usunac SA");
                redirectAttributes.addFlashAttribute("blad", true);
                redirectAttributes.addFlashAttribute("message", "Nie mozesz usunac SA");
            } else {
                String usunieto = "Usunieto uzytkownika " + user.getEmail();
                notificationService.deleteMessagesBySender(user);
                solutionTestRepository.deleteSolutionTestsByUser(user);
                notificationService.deleteMessagesByUserId(user.getId());
                UserService.delete(user.getId());
                redirectAttributes.addFlashAttribute("sukces", true);
                redirectAttributes.addFlashAttribute("message", usunieto);
            }
        }
        return "redirect:/users";
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SUPERADMIN')")
    @RequestMapping(value = "/changeBlock/{id}", method = RequestMethod.GET)
    public String unblockUser(@PathVariable final Integer id, RedirectAttributes redirectAttributes) {
        logger.debug("nazwa metody = unblockUser");
        final CurrentUser currentUser = (CurrentUser) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        final User doer = currentUser.getUser();
        try {
            User user = UserService.getUserById(id)
                    .orElseThrow(
                            () -> new NoSuchElementException(String.format("Uzytkownik o id =%s nie istnieje", id)));

            if (doer.getRole().equals(Role.SUPERADMIN)) {
                if (user.isAccountNonLocked()) {
                    user.setAccountNonLocked(false);
                    String unlock = "Zablokowano uzytkownika " + user.getEmail();
                    redirectAttributes.addFlashAttribute("sukces", true);
                    redirectAttributes.addFlashAttribute("message", unlock);
                } else {
                    user.setAccountNonLocked(true);
                    user.setLogin_tries(3);
                    String lock = "Odblokowano uzytkownika " + user.getEmail();
                    redirectAttributes.addFlashAttribute("sukces", true);
                    redirectAttributes.addFlashAttribute("message", lock);
                }
            } else if (doer.getRole().equals(Role.ADMIN)) {
                if (user.getRole().equals(Role.USER)) {
                    if (user.isAccountNonLocked()) {
                        user.setAccountNonLocked(false);
                        String unlock = "Zablokowano uzytkownika " + user.getEmail();
                        redirectAttributes.addFlashAttribute("sukces", true);
                        redirectAttributes.addFlashAttribute("message", unlock);
                    } else {
                        user.setAccountNonLocked(true);
                        user.setLogin_tries(3);
                        String lock = "Odblokowano uzytkownika " + user.getEmail();
                        redirectAttributes.addFlashAttribute("sukces", true);
                        redirectAttributes.addFlashAttribute("message", lock);
                    }
                } else {
                    String permissionDenied = "Nie masz uprawnień";
                    redirectAttributes.addFlashAttribute("blad", true);
                    redirectAttributes.addFlashAttribute("message", permissionDenied);
                }
            }
            UserService.update(user);
        } catch (final Exception e) {
            logger.error(e.getMessage(), e);
        }
        return "redirect:/users";
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SUPERADMIN')")
    @RequestMapping(value = "/changeActive/{id}", method = RequestMethod.GET)
    public String activateUser(@PathVariable final Integer id, RedirectAttributes redirectAttributes) {
        logger.debug("nazwa metody = activateUser");
        try {
            final CurrentUser currentUser = (CurrentUser) SecurityContextHolder.getContext()
                    .getAuthentication()
                    .getPrincipal();
            final User doer = currentUser.getUser();
            User user = UserService.getUserById(id)
                    .orElseThrow(
                            () -> new NoSuchElementException(String.format("Uzytkownik o id =%s nie istnieje", id)));

            if (doer.getRole().equals(Role.SUPERADMIN)) {
                if (user.isEnabled()) {
                    user.setEnabled(false);
                    String enable = "Deaktywowano uzytkownika " + user.getEmail();
                    redirectAttributes.addFlashAttribute("sukces", true);
                    redirectAttributes.addFlashAttribute("message", enable);
                } else {
                    user.setEnabled(true);
                    String disable = "Aktywowano uzytkownika " + user.getEmail();
                    redirectAttributes.addFlashAttribute("sukces", true);
                    redirectAttributes.addFlashAttribute("message", disable);
                }
            } else if (doer.getRole().equals(Role.ADMIN)) {
                if (user.getRole().equals(Role.USER)) {
                    if (user.isEnabled()) {
                        user.setEnabled(false);
                        String enable = "Deaktywowano uzytkownika " + user.getEmail();
                        redirectAttributes.addFlashAttribute("sukces", true);
                        redirectAttributes.addFlashAttribute("message", enable);

                    } else {
                        user.setEnabled(true);
                        String disable = "Aktywowano uzytkownika " + user.getEmail();
                        redirectAttributes.addFlashAttribute("sukces", true);
                        redirectAttributes.addFlashAttribute("message", disable);
                    }
                } else {
                    String permissionDenied = "Nie masz uprawnień";
                    redirectAttributes.addFlashAttribute("blad", true);
                    redirectAttributes.addFlashAttribute("message", permissionDenied);
                }
            }

            UserService.update(user);
        } catch (final Exception e) {
            logger.error(e.getMessage(), e);
        }
        return "redirect:/users";
    }

}

