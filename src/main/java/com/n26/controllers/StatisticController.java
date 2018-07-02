package com.n26.controllers;

import com.n26.dto.StatisticDto;
import com.n26.dto.TransactionDto;
import com.n26.service.StatisticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class StatisticController {

    private StatisticService statisicsService;

    @Autowired
    public StatisticController(StatisticService statisicsService) {
        this.statisicsService = statisicsService;
    }

    @RequestMapping(value = "/transactions", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity addTransaction (@RequestBody TransactionDto transactionDto) {
        if(statisicsService.addTransaction(transactionDto)){
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }
        else {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

    }

    @RequestMapping(value = "/statistics", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public StatisticDto getStatistics() {
           return statisicsService.getStatistic();
    }
}
