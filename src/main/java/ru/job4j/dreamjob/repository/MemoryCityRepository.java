package ru.job4j.dreamjob.repository;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Repository;
import ru.job4j.dreamjob.model.City;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ThreadSafe
@Repository
public class MemoryCityRepository implements CityRepository {

     /*С расчётом на то, что В ДАЛЬНЕЙШЕМ можно будет добавлять города из вьюшки, а не "вшивать в код".*/
    private final Map<Integer, City> cities = new ConcurrentHashMap<>() {
        {
             /*Необычная инициализация */
            put(1, new City(1, "Москва-ква"));
            put(2, new City(2, "Ленинград"));
            put(3, new City(3, "Мухосранск"));
            put(4, new City(4, "Сталинград"));
            put(5, new City(5, "Ворошиловград"));
            put(6, new City(6, "Кадиевка"));
        }
    };

    @Override
    public Collection<City> findAll() {
        return cities.values();
    }
}
