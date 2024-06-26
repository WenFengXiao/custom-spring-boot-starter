package com.coldcicada.app.controller;

import com.coldcicada.sequence.spring.starter.utils.SequenceFactory;
import lombok.Getter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author coldcicada
 * @Date 2024-06-26 17:17
 * @Description
 */
@RestController
public class SequenceTestController {


    @GetMapping("/getNextSequenceTest")
    public String getNextSequenceTest(@RequestParam("kind") String kind) {

        return SequenceFactory.getInstance().nextSequence(kind);

    }

}
