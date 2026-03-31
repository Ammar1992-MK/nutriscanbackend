package com.nutriscan.mpv.calculations;

public class Threshold {

    //Scientific based values (WHO/EFSA) per 100g
    public static final double ENERGY_LOSE = 400;  // kcal / 100g
    public static final double SUGAR_LOSE  = 10;   // g / 100g
    public static final double FAT_LOSE    = 16;   // g / 100g
    public static final double PROTEIN_MIN = 5;    // g / 100g
    public static final double CARBS_LOSE = 25;

    // --- MAINTAIN goal ---
    public static final double ENERGY_MAINTAIN = 500;
    public static final double SUGAR_MAINTAIN  = 12.5;
    public static final double FAT_MAINTAIN    = 19.5;
    public static final double PROTEIN_MAINTAIN  = 12;
    public static final double CARBS_MAINTAIN = 35;

    // --- GAIN goal ---
    public static final double ENERGY_GAIN = 700;
    public static final double SUGAR_GAIN  = 17.5;
    public static final double FAT_GAIN    = 27;
    public static final double PROTEIN_GAIN   = 20;
    public static final double CARBS_GAIN = 50;
}
