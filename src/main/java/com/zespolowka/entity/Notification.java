package com.zespolowka.entity;

import com.zespolowka.entity.user.Role;
import com.zespolowka.entity.user.User;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
        SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy HH:mm:ss");
        String now = sdf.format(new Date());
        try {
            this.date = sdf.parse(now);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        this.message = message;
        this.topic = topic;
        this.userId = userId;
        this.unread = true;
        this.userRole = null;
        this.sender = sender;
    }

    public Notification(String message, String topic, Date date, long userId, User sender) {
        this.message = message;
        this.topic = topic;
        this.date = date;
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