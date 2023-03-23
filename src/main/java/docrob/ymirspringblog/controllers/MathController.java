package docrob.ymirspringblog.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@ResponseBody
public class MathController {
    @GetMapping("/add/{operand1}/and/{operand2}")
    public String add(@PathVariable int operand1, @PathVariable int operand2) {
        return String.format("%d + %d is %d", operand1, operand2, operand1 + operand2);
    }

    @GetMapping("/subtract/{operand1}/from/{operand2}")
    public String subtract(@PathVariable int operand1, @PathVariable int operand2) {
        return String.format("%d - %d is %d", operand2, operand1, operand2 - operand1);
    }

    @GetMapping("/multiply/{operand1}/and/{operand2}")
    public String multiply(@PathVariable int operand1, @PathVariable int operand2) {
        return String.format("%d x %d is %d", operand1, operand2, operand1 * operand2);
    }

    @GetMapping("/divide/{operand1}/by/{operand2}")
    public String divide(@PathVariable int operand1, @PathVariable int operand2) {
        return String.format("%d / %d is %f", operand1, operand2, (double) operand1 / operand2);
    }
}
