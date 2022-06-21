package com.example.command.rest;

import com.example.command.dto.ResultCommand;
import com.example.command.service.CommandService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("api/docker")
public class CommandController {

    private final CommandService commandService;

    public CommandController(CommandService commandService) {
        this.commandService = commandService;
    }

    @CrossOrigin()
    @RequestMapping(value = "/command", method = RequestMethod.POST)
    public ResponseEntity<ResultCommand> shellCommand(@RequestBody String command) throws Exception {
        return Optional
                .ofNullable(commandService.shellCommand(command))
                .map(list -> ResponseEntity.ok().body(list))          //200 OK
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}