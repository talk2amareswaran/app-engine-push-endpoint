package com.educative;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

@RestController
public class PushEndpointController {

	private final Gson gson = new Gson();

	private PubSubCustomMessage getMessage(HttpServletRequest request) throws IOException {
		String requestBody = request.getReader().lines().collect(Collectors.joining("\n"));
		JsonElement jsonRoot = JsonParser.parseString(requestBody);
		String messageStr = jsonRoot.getAsJsonObject().get("message").toString();
		PubSubCustomMessage message = gson.fromJson(messageStr, PubSubCustomMessage.class);
		String decoded = new String(Base64.getDecoder().decode(message.getData()));
		message.setData(decoded);
		return message;
	}

	List<PubSubCustomMessage> messageList = new ArrayList<>();

	@RequestMapping(value = "/messages", method = RequestMethod.GET)
	public ResponseEntity<Object> getMessages() {
		return new ResponseEntity<>(messageList, HttpStatus.OK);
	}

	@RequestMapping(value = "/messages", method = RequestMethod.POST)
	public ResponseEntity<Object> pushMessages(HttpServletRequest request) throws IOException {
		messageList.add(getMessage(request));
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ResponseEntity<Object> home() {
		return new ResponseEntity<>("Welcome to App Engine Deployment!", HttpStatus.OK);
	}
}