package com.zespolowka.forms;

import com.zespolowka.entity.user.User;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class NewMessageForm {

    private String message;
    private String topic;
    private String receivers;
    private User sender;

}
