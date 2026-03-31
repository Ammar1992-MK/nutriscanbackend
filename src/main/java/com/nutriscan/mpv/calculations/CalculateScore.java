package com.nutriscan.mpv.calculations;

import com.nutriscan.mpv.GoalType;
import com.nutriscan.mpv.Product;
import com.nutriscan.mpv.User;

import static com.nutriscan.mpv.calculations.Threshold.*;

public class CalculateScore {

    public static final CalculateScore INSTANCE = new CalculateScore();

    public int calculate(Product product, User user) {

        double score = 100;

        GoalType goal = user.getNutritionProfile().getGoal();

        // --- Local limits (avoids mutable state on singleton) ---
        final double energyLimit;
        final double sugarLimit;
        final double fatLimit;
        final double carbLimit;
        final double proteinLimit;

        switch (goal) {
            case LOSE_WEIGHT -> {
                energyLimit  = ENERGY_LOSE;
                sugarLimit   = SUGAR_LOSE;
                fatLimit     = FAT_LOSE;
                carbLimit    = CARBS_LOSE;
                proteinLimit = PROTEIN_MIN;
            }
            case MAINTAIN_WEIGHT -> {
                energyLimit  = ENERGY_MAINTAIN;
                sugarLimit   = SUGAR_MAINTAIN;
                fatLimit     = FAT_MAINTAIN;
                carbLimit    = CARBS_MAINTAIN;
                proteinLimit = PROTEIN_MAINTAIN;
            }
            case GAIN_WEIGHT -> {
                energyLimit  = ENERGY_GAIN;
                sugarLimit   = SUGAR_GAIN;
                fatLimit     = FAT_GAIN;
                carbLimit    = CARBS_GAIN;
                proteinLimit = PROTEIN_GAIN;
            }
            default -> throw new IllegalArgumentException("Unsupported goal: " + goal);
        }

        // --- Null-safe nutrient values ---
        double energy  = product.getEnergy()  != null ? product.getEnergy()  : 0.0;
        double carbs   = product.getCarbs()   != null ? product.getCarbs()   : 0.0;
        double fat     = product.getFat()     != null ? product.getFat()     : 0.0;
        double sugar   = product.getSugar()   != null ? product.getSugar()   : 0.0;
        double protein = product.getProtein() != null ? product.getProtein() : 0.0;

        // --- Over-limit penalties (capped so no single nutrient exceeds its allocated deduction) ---
        score -= Math.min(Penalties.ENERGY_PENALTY, Penalties.ENERGY_PENALTY * Math.max(0, (energy - energyLimit) / energyLimit));
        score -= Math.min(Penalties.CARBS_PENALTY,  Penalties.CARBS_PENALTY  * Math.max(0, (carbs  - carbLimit)   / carbLimit));
        score -= Math.min(Penalties.FAT_PENALTY,    Penalties.FAT_PENALTY    * Math.max(0, (fat    - fatLimit)    / fatLimit));
        score -= Math.min(Penalties.SUGAR_PENALTY,  Penalties.SUGAR_PENALTY  * Math.max(0, (sugar  - sugarLimit)  / sugarLimit));

        // --- Protein: penalise if below minimum (capped at its max penalty) ---
        if (protein < proteinLimit) {
            score -= Math.min(Penalties.PROTEIN_PENALTY, Penalties.PROTEIN_PENALTY * (proteinLimit - protein) / proteinLimit);
        }

        // --- NOVA group penalty (ultra-processed food) ---
        if (product.getNovaGroup() != null) {
            if (product.getNovaGroup() == 4) score -= Penalties.NOVA_4_PENALTY;
            else if (product.getNovaGroup() == 3) score -= Penalties.NOVA_3_PENALTY;
        }

        // --- Clamp final score to 0–100 ---
        return (int) Math.max(0, Math.min(score, 100));
    }
}