package com.zespolowka.controller;

import javax.validation.Valid;
import java.util.NoSuchElementException;
import java.util.Objects;

import com.zespolowka.entity.user.CurrentUser;
import com.zespolowka.entity.user.Role;
import com.zespolowka.entity.user.User;
import com.zespolowka.forms.UserEditForm;
import com.zespolowka.repository.SolutionTestRepository;
import com.zespolowka.service.NotificationService;
import com.zespolowka.service.UserService;
import com.zespolowka.validators.ChangePasswordValidator;
import com.zespolowka.validators.UserEditValidator;
import lombok.extern.slf4j.Slf4j;

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

/**
 * Created by Pitek on 2015-12-01.
 */
@Controller
@RequestMapping(value = "/user")
@Slf4j
public class UserController {
	private static final String USER_NOTEXIST = "Uzytkownik o id =%s nie istnieje";
	private static final String USER_EDIT = "userEdit";
	private static final String SUKCES = "sukces";
	private static final String MESSAGE = "message";
	private static final String BLAD = "blad";
	private static final String REDIRECT_USERS = "redirect:/users";
	private final UserService userService;

	@Autowired
	private final NotificationService notificationService;

	@Autowired
	private final ChangePasswordValidator changePasswordValidator;

	private final UserEditValidator userEditValidator;

	private final SolutionTestRepository solutionTestRepository;


	@Autowired
	public UserController(final UserService userService, NotificationService notificationService, ChangePasswordValidator changePasswordValidator, UserEditValidator userEditValidator, SolutionTestRepository solutionTestRepository) {
		this.userService = userService;
		this.notificationService = notificationService;
		this.changePasswordValidator = changePasswordValidator;
		this.userEditValidator = userEditValidator;
		this.solutionTestRepository = solutionTestRepository;
	}

	@PreAuthorize("@currentUserServiceImpl.canAccessUser(principal, #id)")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public String showUserDetail(@PathVariable final Long id, final Model model) {
		try {
			log.info("nazwa metody = showUserDetail");
			final User user = userService.getUserById(id).orElseThrow(() -> new NoSuchElementException(String.format(USER_NOTEXIST, id)));
			model.addAttribute(user);
		}
		catch (final Exception e) {
			log.error(e.getMessage(), e);
			log.info("{}" + '\n' + "{}", id.toString(), model);
		}
		return "userDetail";
	}

	@RequestMapping(value = "/show", method = RequestMethod.GET)
	public String showCurrentUserDetail(final Model model) {
		try {
			log.info("nazwa metody = showCurrentUserDetail");
			final CurrentUser currentUser = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			final User user = currentUser.getUser();
			model.addAttribute(user);
			model.addAttribute("Notifications", notificationService.findTop5ByUserIdOrUserRoleOrderByDateDesc(user.getId(), user.getRole()));
		}
		catch (final RuntimeException e) {
			log.error(e.getMessage(), e);
		}
		return "userDetail";
	}


	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public String editCurrentUserDetail(final Model model) {
		log.info("nazwa metody = showCurrentUserDetail");
		try {
			final CurrentUser currentUser = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			final User user = currentUser.getUser();
			model.addAttribute("userEditForm", new UserEditForm(user));
		}
		catch (final RuntimeException e) {
			log.error(e.getMessage(), e);
		}
		return USER_EDIT;
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	public String saveCurrentUser(@ModelAttribute @Valid final UserEditForm userEditForm, final Errors errors, final Model model) {
		log.info("nazwa metody = saveCurrentUser");
		changePasswordValidator.validate(userEditForm, errors);
		userEditValidator.validate(userEditForm, errors);
		if (errors.hasErrors()) {
			String err = errors.getAllErrors().get(0).toString();
			log.info("err:{}", err);
			return USER_EDIT;
		}
		else {
			final User user = userService.editUser(userEditForm);
			CurrentUser currentUser = new CurrentUser(user);
			Authentication authentication = new UsernamePasswordAuthenticationToken(currentUser, currentUser.getPassword(), currentUser.getAuthorities());
			SecurityContextHolder.getContext().setAuthentication(authentication);
			model.addAttribute(SUKCES, true);
			return USER_EDIT;
		}
	}

	@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SUPERADMIN')")
	@RequestMapping(value = "/edit/{id}", method = RequestMethod.GET)
	public String editUser(@PathVariable final Integer id, final Model model) {
		log.debug("nazwa metody = editUser");
		try {
			model.addAttribute("userEditForm", new UserEditForm(userService.getUserById(id).orElseThrow(() -> new NoSuchElementException(String.format(USER_NOTEXIST, id)))));
		}
		catch (final Exception e) {
			log.error(e.getMessage(), e);
			log.info("{}" + '\n' + "{}" + '\n' + "{}", id.toString(), model, userService.getUserById(id));
		}
		return USER_EDIT;
	}

	@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SUPERADMIN')")
	@RequestMapping(value = "/edit/{id}", method = RequestMethod.POST)
	public String saveUser(@PathVariable final Integer id, @ModelAttribute @Valid final UserEditForm userEditForm, final Errors errors) {
		log.info("nazwa metody = saveUser");
		if (errors.hasErrors()) {
			return USER_EDIT;
		}
		else {
			final User user = userService.editUser(userEditForm);
			return "redirect:/user/" + user.getId();
		}

	}

	@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SUPERADMIN')")
	@RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
	public String deleteUser(@PathVariable final Long id, RedirectAttributes redirectAttributes) {
		final CurrentUser currentUser = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		log.info("nazwa metody = deleteUser");
		log.info("userDelete{}", id);
		if (Objects.equals(currentUser.getId(), id)) {
			log.info("Nie mozesz usunac siebie");
			redirectAttributes.addFlashAttribute(BLAD, true);
			redirectAttributes.addFlashAttribute(MESSAGE, "Nie mozesz usunac siebie");
		}
		else {
			User user = userService.getUserById(id).orElseThrow(() -> new NoSuchElementException(String.format(USER_NOTEXIST, id)));
			if ("ADMIN".equals(currentUser.getRole().name()) && "SUPERADMIN".equals(user.getRole().name())) {
				log.info("Nie mozesz usunac SA");
				redirectAttributes.addFlashAttribute(BLAD, true);
				redirectAttributes.addFlashAttribute(MESSAGE, "Nie mozesz usunac SA");
			}
			else {
				String usunieto = "Usunieto uzytkownika " + user.getEmail();
				notificationService.deleteMessagesBySender(user);
				solutionTestRepository.deleteSolutionTestsByUser(user);
				notificationService.deleteMessagesByUserId(user.getId());
				userService.delete(user.getId());
				redirectAttributes.addFlashAttribute(SUKCES, true);
				redirectAttributes.addFlashAttribute(MESSAGE, usunieto);
			}
		}
		return REDIRECT_USERS;
	}

	@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SUPERADMIN')")
	@RequestMapping(value = "/changeBlock/{id}", method = RequestMethod.GET)
	public String unblockUser(@PathVariable final Integer id, RedirectAttributes redirectAttributes) {
		log.debug("nazwa metody = unblockUser");
		final CurrentUser currentUser = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		final User doer = currentUser.getUser();
		User user = userService.getUserById(id).orElseThrow(() -> new NoSuchElementException(String.format(USER_NOTEXIST, id)));

		if (doer.getRole().equals(Role.SUPERADMIN)) {
			if (user.isAccountNonLocked()) {
				user.setAccountNonLocked(false);
				String unlock = "Zablokowano uzytkownika " + user.getEmail();
				redirectAttributes.addFlashAttribute(SUKCES, true);
				redirectAttributes.addFlashAttribute(MESSAGE, unlock);
			}
			else {
				user.setAccountNonLocked(true);
				user.setLoginTries(3);
				String lock = "Odblokowano uzytkownika " + user.getEmail();
				redirectAttributes.addFlashAttribute(SUKCES, true);
				redirectAttributes.addFlashAttribute(MESSAGE, lock);
			}
		}
		else {
			String permissionDenied = "Nie masz uprawnień";
			redirectAttributes.addFlashAttribute(BLAD, true);
			redirectAttributes.addFlashAttribute(MESSAGE, permissionDenied);
		}
		userService.update(user);

		return REDIRECT_USERS;
	}

	@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SUPERADMIN')")
	@RequestMapping(value = "/changeActive/{id}", method = RequestMethod.GET)
	public String activateUser(@PathVariable final Integer id, RedirectAttributes redirectAttributes) {
		log.debug("nazwa metody = activateUser");
		final CurrentUser currentUser = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		final User doer = currentUser.getUser();
		User user = userService.getUserById(id).orElseThrow(() -> new NoSuchElementException(String.format(USER_NOTEXIST, id)));

		if (doer.getRole().equals(Role.SUPERADMIN)) {
			if (user.isEnabled()) {
				user.setEnabled(false);
				String enable = "Deaktywowano uzytkownika " + user.getEmail();
				redirectAttributes.addFlashAttribute(SUKCES, true);
				redirectAttributes.addFlashAttribute(MESSAGE, enable);
			}
			else {
				user.setEnabled(true);
				String disable = "Aktywowano uzytkownika " + user.getEmail();
				redirectAttributes.addFlashAttribute(SUKCES, true);
				redirectAttributes.addFlashAttribute(MESSAGE, disable);
			}
		}
		else {
			String permissionDenied = "Nie masz uprawnień";
			redirectAttributes.addFlashAttribute(BLAD, true);
			redirectAttributes.addFlashAttribute(MESSAGE, permissionDenied);
		}
		userService.update(user);
		return REDIRECT_USERS;
	}

}

