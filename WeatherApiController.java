package com.example.weather_app.controller;

import com.example.weather_app.service.WeatherService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Mono;

@Controller
public class WeatherApiController {

    @Autowired
    private WeatherService weatherService;

    // ✅ Home page (index.html)
    @GetMapping("/")
    public String home() {
        return "index";
    }

    // ✅ Fetch weather info for given city and display in weather.html
    @GetMapping("/weather")
    public String getWeather(@RequestParam("city") String city, Model model) {
        try {
            // Fetch weather data from API
            Mono<String> weatherData = weatherService.getCurrentWeatherByCity(city);
            String response = weatherData.block(); // block() waits for async result

            // Parse JSON response
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);

            // Extract key weather information
            model.addAttribute("city", root.path("name").asText());
            model.addAttribute("temperature", root.path("main").path("temp").asDouble());
            model.addAttribute("humidity", root.path("main").path("humidity").asInt());
            model.addAttribute("description", root.path("weather").get(0).path("description").asText());
            model.addAttribute("windSpeed", root.path("wind").path("speed").asDouble());

        } catch (Exception e) {
            model.addAttribute("error", "Could not fetch or parse weather data. Please try again.");
        }

        // Return weather.html template
        return "weather";
    }
}
