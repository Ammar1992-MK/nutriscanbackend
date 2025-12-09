package com.nutriscan.mpv.repository;

import com.nutriscan.mpv.NutritionProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NutritionProfileRepository extends JpaRepository<NutritionProfile, Long> {

}
