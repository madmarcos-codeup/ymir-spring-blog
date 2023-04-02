package docrob.ymirspringblog.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Controller
public class DiceController {
    private Random random = new Random();

    @GetMapping("/roll-dice")
    public String rollDice() {
        return "dice/dice";
    }

    @GetMapping("/roll-dice/{guess}")
    public String rollDice(@PathVariable int guess, Model model) {

        int answer = random.nextInt(6) + 1;
        model.addAttribute("guess", guess);
        model.addAttribute("answer", answer);

        String winMessage = "Sorry, you lose!";
        if(guess == answer) {
            winMessage = "Wow, you won!";
        }
        model.addAttribute("winMsg", winMessage);

        // bonus
        List<Integer> rolls = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            rolls.add(random.nextInt(6) + 1);
        }
        model.addAttribute("rolls", rolls);

        return "dice/guess";
    }
}
