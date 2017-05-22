package com.zespolowka.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.Date;

import com.zespolowka.entity.user.Role;
import com.zespolowka.entity.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "NotificationTable")
@NoArgsConstructor
@Getter
public class Notification {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Lob
	@Column(length = 10000)
	private String message;
	private String topic;
	private Date date;
	private boolean status;
	@OneToOne(fetch = FetchType.LAZY)
	@MapsId
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
		this.status = true;
		this.userRole = null;
		this.sender = sender;
	}

	public void changeStatus(Boolean status) {
		this.status = status;
	}

}