package com.nutriscan.mpv.calculations;

import com.nutriscan.mpv.GoalType;
import com.nutriscan.mpv.NutritionProfile;
import com.nutriscan.mpv.Product;
import com.nutriscan.mpv.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CalculateScoreTest {

    private CalculateScore calculator;

    private User userWithGoal(GoalType goal) {
        NutritionProfile profile = new NutritionProfile();
        profile.setGoal(goal);

        User user = new User();
        user.setNutritionProfile(profile);
        return user;
    }

    private Product idealForLoseWeight() {
        Product p = new Product();
        p.setEnergy(200.0);
        p.setCarbs(15.0);
        p.setFat(8.0);
        p.setSugar(5.0);
        p.setProtein(25.0);
        p.setNovaGroup(1);
        return p;
    }

    private Product idealForMaintainWeight() {
        Product p = new Product();
        p.setEnergy(400.0);
        p.setCarbs(25.0);
        p.setFat(14.0);
        p.setSugar(9.0);
        p.setProtein(15.0);
        p.setNovaGroup(1);
        return p;
    }

    private Product idealForGainWeight() {
        Product p = new Product();
        p.setEnergy(600.0);
        p.setCarbs(40.0);
        p.setFat(22.0);
        p.setSugar(14.0);
        p.setProtein(22.0);
        p.setNovaGroup(1);
        return p;
    }

    @BeforeEach
    void setUp() {
        calculator = CalculateScore.INSTANCE;
    }

    @Test
    @DisplayName("INSTANCE is not null")
    void instanceIsNotNull() {
        assertNotNull(CalculateScore.INSTANCE);
    }

    @Nested
    @DisplayName("Goal-ideal product scores 100 for its matching goal")
    class IdealProducts {

        @Test
        void loseWeightIdealScores100() {
            assertEquals(100, calculator.calculate(idealForLoseWeight(), userWithGoal(GoalType.LOSE_WEIGHT)));
        }

        @Test
        void maintainWeightIdealScores100() {
            assertEquals(100, calculator.calculate(idealForMaintainWeight(), userWithGoal(GoalType.MAINTAIN_WEIGHT)));
        }

        @Test
        void gainWeightIdealScores100() {
            assertEquals(100, calculator.calculate(idealForGainWeight(), userWithGoal(GoalType.GAIN_WEIGHT)));
        }

        @Test
        void gainWeightIdealScoresPoorlyForLoseWeight() {
            int scoreLose = calculator.calculate(idealForGainWeight(), userWithGoal(GoalType.LOSE_WEIGHT));
            assertTrue(scoreLose < 100, "A gain-weight product should not score 100 for lose-weight");
        }

        @Test
        void loseWeightIdealScoresPoorlyForGainWeight() {
            int scoreGain = calculator.calculate(idealForLoseWeight(), userWithGoal(GoalType.GAIN_WEIGHT));
            assertTrue(scoreGain < 100, "A lose-weight product should not score 100 for gain-weight");
        }
    }

    @Test
    @DisplayName("Score is clamped to 0 for an extremely bad product")
    void scoreIsClampedToZero() {
        Product terrible = new Product();
        terrible.setEnergy(9999.0);
        terrible.setCarbs(9999.0);
        terrible.setFat(9999.0);
        terrible.setSugar(9999.0);
        terrible.setProtein(0.0);
        terrible.setNovaGroup(4);

        int score = calculator.calculate(terrible, userWithGoal(GoalType.LOSE_WEIGHT));
        assertTrue(score >= 0, "Score must not go below 0");
        assertTrue(score <= 100, "Score must not exceed 100");
    }

    @Test
    @DisplayName("Score never exceeds 100 for any goal-ideal product")
    void scoreIsClampedToHundred() {
        assertTrue(calculator.calculate(idealForLoseWeight(),     userWithGoal(GoalType.LOSE_WEIGHT))     <= 100);
        assertTrue(calculator.calculate(idealForMaintainWeight(), userWithGoal(GoalType.MAINTAIN_WEIGHT)) <= 100);
        assertTrue(calculator.calculate(idealForGainWeight(),     userWithGoal(GoalType.GAIN_WEIGHT))     <= 100);
    }

    @Nested
    @DisplayName("NOVA group penalties")
    class NovaPenalties {

        @Test
        @DisplayName("NOVA 4 applies a 25-point penalty")
        void nova4Penalty() {
            Product nova1 = idealForMaintainWeight();
            nova1.setNovaGroup(1);

            Product nova4 = idealForMaintainWeight();
            nova4.setNovaGroup(4);

            int diff = calculator.calculate(nova1, userWithGoal(GoalType.MAINTAIN_WEIGHT))
                     - calculator.calculate(nova4, userWithGoal(GoalType.MAINTAIN_WEIGHT));

            assertEquals((int) Penalties.NOVA_4_PENALTY, diff);
        }

        @Test
        @DisplayName("NOVA 3 applies a 5-point penalty")
        void nova3Penalty() {
            Product nova1 = idealForMaintainWeight();
            nova1.setNovaGroup(1);

            Product nova3 = idealForMaintainWeight();
            nova3.setNovaGroup(3);

            int diff = calculator.calculate(nova1, userWithGoal(GoalType.MAINTAIN_WEIGHT))
                     - calculator.calculate(nova3, userWithGoal(GoalType.MAINTAIN_WEIGHT));

            assertEquals((int) Penalties.NOVA_3_PENALTY, diff);
        }

        @Test
        @DisplayName("Null NOVA group applies no penalty")
        void nullNovaGroupNoPenalty() {
            Product noNova = idealForMaintainWeight();
            noNova.setNovaGroup(null);

            Product nova1 = idealForMaintainWeight();
            nova1.setNovaGroup(1);

            assertEquals(
                calculator.calculate(nova1,  userWithGoal(GoalType.MAINTAIN_WEIGHT)),
                calculator.calculate(noNova, userWithGoal(GoalType.MAINTAIN_WEIGHT))
            );
        }
    }

    @Test
    @DisplayName("All-null nutrient fields do not throw NullPointerException")
    void nullNutrientsDoNotThrow() {
        Product nullProduct = new Product();
        assertDoesNotThrow(() -> calculator.calculate(nullProduct, userWithGoal(GoalType.LOSE_WEIGHT)));
    }

    @Test
    @DisplayName("Null protein is treated as 0 and receives a protein penalty")
    void nullProteinReceivesPenalty() {
        Product withProtein = idealForLoseWeight();
        Product nullProtein = idealForLoseWeight();
        nullProtein.setProtein(null);

        int scoreWithProtein = calculator.calculate(withProtein, userWithGoal(GoalType.LOSE_WEIGHT));
        int scoreNullProtein = calculator.calculate(nullProtein, userWithGoal(GoalType.LOSE_WEIGHT));

        assertTrue(scoreNullProtein < scoreWithProtein,
                "Null protein (treated as 0) should score lower than a product with adequate protein");
    }

    @Test
    @DisplayName("Product with protein below minimum scores lower than one above minimum")
    void lowProteinScoresLower() {
        Product lowProtein = idealForLoseWeight();
        lowProtein.setProtein(0.0);

        Product goodProtein = idealForLoseWeight();
        goodProtein.setProtein(20.0);

        int scoreLow  = calculator.calculate(lowProtein,  userWithGoal(GoalType.LOSE_WEIGHT));
        int scoreGood = calculator.calculate(goodProtein, userWithGoal(GoalType.LOSE_WEIGHT));

        assertTrue(scoreLow < scoreGood, "Low protein should reduce the score");
    }

    @Test
    @DisplayName("Excessive energy reduces score for LOSE_WEIGHT goal")
    void excessiveEnergyReducesScore() {
        Product normal  = idealForLoseWeight();
        Product highCal = idealForLoseWeight();
        highCal.setEnergy(Threshold.ENERGY_LOSE * 3);

        int scoreNormal  = calculator.calculate(normal,  userWithGoal(GoalType.LOSE_WEIGHT));
        int scoreHighCal = calculator.calculate(highCal, userWithGoal(GoalType.LOSE_WEIGHT));

        assertTrue(scoreHighCal < scoreNormal, "High energy should reduce the score");
    }

    @Test
    @DisplayName("Excessive sugar reduces score for LOSE_WEIGHT goal")
    void excessiveSugarReducesScore() {
        Product normal    = idealForLoseWeight();
        Product highSugar = idealForLoseWeight();
        highSugar.setSugar(Threshold.SUGAR_LOSE * 3);

        assertTrue(
            calculator.calculate(highSugar, userWithGoal(GoalType.LOSE_WEIGHT)) <
            calculator.calculate(normal,    userWithGoal(GoalType.LOSE_WEIGHT))
        );
    }

    @Test
    @DisplayName("Excessive fat reduces score for LOSE_WEIGHT goal")
    void excessiveFatReducesScore() {
        Product normal  = idealForLoseWeight();
        Product highFat = idealForLoseWeight();
        highFat.setFat(Threshold.FAT_LOSE * 3);

        assertTrue(
            calculator.calculate(highFat, userWithGoal(GoalType.LOSE_WEIGHT)) <
            calculator.calculate(normal,  userWithGoal(GoalType.LOSE_WEIGHT))
        );
    }

    @Test
    @DisplayName("Excessive carbs reduces score for LOSE_WEIGHT goal")
    void excessiveCarbsReducesScore() {
        Product normal    = idealForLoseWeight();
        Product highCarbs = idealForLoseWeight();
        highCarbs.setCarbs(Threshold.CARBS_LOSE * 3);

        assertTrue(
            calculator.calculate(highCarbs, userWithGoal(GoalType.LOSE_WEIGHT)) <
            calculator.calculate(normal,    userWithGoal(GoalType.LOSE_WEIGHT))
        );
    }

    @Test
    @DisplayName("A gain-weight ideal product scores higher for GAIN_WEIGHT than LOSE_WEIGHT")
    void goalAffectsScore() {
        Product gainIdeal = idealForGainWeight();

        int scoreLose = calculator.calculate(gainIdeal, userWithGoal(GoalType.LOSE_WEIGHT));
        int scoreGain = calculator.calculate(gainIdeal, userWithGoal(GoalType.GAIN_WEIGHT));

        assertTrue(scoreGain > scoreLose,
                "A calorie-dense product should score better for GAIN_WEIGHT than LOSE_WEIGHT");
    }


    @Test
    @DisplayName("Concurrent calls on singleton produce consistent results")
    void threadSafety() throws InterruptedException {
        int[] results = new int[100];
        Thread[] threads = new Thread[100];

        for (int i = 0; i < 100; i++) {
            final int idx = i;
            final GoalType goal;
            final Product product;
            if (i % 3 == 0) {
                goal = GoalType.LOSE_WEIGHT;
                product = idealForLoseWeight();
            } else if (i % 3 == 1) {
                goal = GoalType.MAINTAIN_WEIGHT;
                product = idealForMaintainWeight();
            } else {
                goal = GoalType.GAIN_WEIGHT;
                product = idealForGainWeight();
            }
            threads[i] = new Thread(() ->
                results[idx] = calculator.calculate(product, userWithGoal(goal))
            );
        }

        for (Thread t : threads) t.start();
        for (Thread t : threads) t.join();

        for (int score : results) {
            assertEquals(100, score, "All goal-ideal product scores should be 100 regardless of concurrency");
        }
    }
}

