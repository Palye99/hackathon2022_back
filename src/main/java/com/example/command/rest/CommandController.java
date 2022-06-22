package com.example.command.rest;

import com.example.command.dto.ResultCommand;
import com.example.command.service.CommandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("api/command")
public class CommandController {

    private final CommandService commandService;

    @Autowired
    public CommandController(CommandService commandService) {
        this.commandService = commandService;
    }

    @CrossOrigin()
    @GetMapping
    public ResponseEntity<ResultCommand> execTerraform() throws Exception {
        String command = "tree";
        return Optional
                .ofNullable(commandService.execTerraform(command))
                .map(list -> ResponseEntity.ok().body(list))          //200 OK
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @CrossOrigin()
    @PostMapping
    public ResponseEntity<ResultCommand> shellCommand(@RequestBody String command) throws Exception {
        return Optional
                .ofNullable(commandService.shellCommand(command))
                .map(list -> ResponseEntity.ok().body(list))          //200 OK
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
