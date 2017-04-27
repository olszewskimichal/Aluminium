package com.zespolowka.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.Date;

import com.zespolowka.entity.user.Role;
import com.zespolowka.entity.user.User;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "NotificationTable")
@Data
@NoArgsConstructor
public class Notification {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Lob
	@Column(length = 10000)
	private String message;
	private String topic;
	private Date date;
	private boolean unread;
	@OneToOne(targetEntity = User.class, orphanRemoval = false)
	private User sender;
	private long userId;

	@Column
	@Enumerated(EnumType.STRING)
	private Role userRole;


	public Notification(String message, String topic, long userId, User sender) { //data auto
		this.date = new Date();
		this.message = message;
		this.topic = topic;
		this.userId = userId;
		this.unread = true;
		this.userRole = null;
		this.sender = sender;
	}

	public Notification(String message, String topic, Date date, Role userRole, User sender) {
		this.message = message;
		this.topic = topic;
		this.date = date;
		this.userRole = userRole;
		this.unread = true;
		this.userId = -1L;
		this.sender = sender;
	}
}