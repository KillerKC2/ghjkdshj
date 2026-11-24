package ru.rvsteal;

import java.util.Optional;
import net.minecraft.class_1657;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_3966;
import net.minecraft.class_239.class_240;

public final class Hitboxes {
   private Hitboxes() {
   }

   public static void pick(float delta) {
      class_310 c = class_310.method_1551();
      // Проверяем, включен ли чит
      if (RvSteal.active() && ModuleSettings.hitboxes && c.field_1724 != null && c.field_1687 != null && c.method_1560() == c.field_1724) {
         class_243 eye = c.field_1724.method_5836(delta);
         
         // ВАЖНО: Реальный лимит атаки (Reach) в vanilla ~3.0. 
         // Для Vulcan безопасно ставить 3.15 - 3.2. Если поставить больше, начнутся флаги.
         double maxReach = 3.15; 
         
         double range = c.field_1724.method_55755(); // Игровая дальность (обычно 4.5 блока для творческого, но мы бьем на 3)
         class_243 end = eye.method_1019(c.field_1724.method_5828(delta).method_1021(range));
         double nearest = range * range;
         
         if (c.field_1765 != null && c.field_1765.method_17783() != class_240.field_1333) {
            nearest = Math.min(nearest, eye.method_1025(c.field_1765.method_17784()));
         }

         class_3966 best = null;

         for (class_1657 other : c.field_1687.method_18456()) {
            if (other != c.field_1724 && !other.method_7325() && other.method_5805() && !other.method_31481()) {
               // 1. Получаем РЕАЛЬНЫЙ хитбокс для проверки дистанции
               class_238 realBox = other.method_5829().method_997(other.method_30950(delta).method_1020(other.method_73189()));
               
               // 2. Получаем РАСШИРЕННЫЙ хитбокс (как было в твоем коде)
               class_238 expandedBox = realBox.method_1014(ModuleSettings.hitboxSize / 100.0);
               
               // Ищем пересечение луча с РАСШИРЕННЫМ хитбоксом (чтобы легче наводиться)
               Optional<class_243> point = expandedBox.method_992(eye, end);
               
               if (point.isPresent()) {
                  // Проверяем дистанцию от глаз до РЕАЛЬНОГО хитбокса
                  Optional<class_243> realPoint = realBox.method_992(eye, end);
                  double realDistance = realPoint.map(p -> Math.sqrt(eye.method_1025(p))).orElse(999.0); // Если луч не попал в реальный, ставим 999

                  // Если реальная дистанция больше maxReach, мы не должны выбирать эту цель
                  if (realDistance > maxReach) {
                     continue; 
                  }

                  // Если цель валидна, проверяем, ближе ли она, чем предыдущая
                  if (eye.method_1025(point.get()) < nearest) {
                     nearest = eye.method_1025(point.get());
                     best = new class_3966(other, point.get());
                  }
               }
            }
         }

         if (best != null) {
            c.field_1765 = best;
            c.field_1692 = best.method_17782();
         }
      }
   }
}