package com.example.nagoyameshi.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.nagoyameshi.entity.Restaurant;
import com.example.nagoyameshi.form.RestaurantEditForm;
import com.example.nagoyameshi.form.RestaurantRegisterForm;
import com.example.nagoyameshi.repository.RestaurantRepository;

@Service
public class RestaurantService {
	private final RestaurantRepository restaurantRepository;
	private final RegularHolidayRestaurantService regularHolidayRestaurantService;
	private final CategoryRestaurantService categoryRestaurantService;
	private final FavoriteService favoriteService;
	private final ReservationService reservationService;
	private final ReviewService reviewService;

	public RestaurantService(RestaurantRepository restaurantRepository,
			RegularHolidayRestaurantService regularHolidayRestaurantService,
			CategoryRestaurantService categoryRestaurantService, FavoriteService favoriteService,
			ReservationService reservationService, ReviewService reviewService) {
		this.restaurantRepository = restaurantRepository;
		this.regularHolidayRestaurantService = regularHolidayRestaurantService;
		this.categoryRestaurantService = categoryRestaurantService;
		this.favoriteService = favoriteService;
		this.reservationService = reservationService;
		this.reviewService = reviewService;
	}

	@Transactional
	public void create(RestaurantRegisterForm restaurantRegisterForm) {
		Restaurant restaurant = new Restaurant();
		MultipartFile image = restaurantRegisterForm.getImage();
		List<Integer> regularHolidayIds = restaurantRegisterForm.getRegularHolidayIds();
		List<Integer> categoryIds = restaurantRegisterForm.getCategoryIds();

		if (!image.isEmpty()) {
			String imageName = image.getOriginalFilename();
			String hashedImageName = generateNewFileName(imageName);
			Path filePath = Paths.get("src/main/resources/static/storage/" + hashedImageName);
			copyImageFile(image, filePath);
			restaurant.setImage(hashedImageName);
		}

		restaurant.setName(restaurantRegisterForm.getName());
		restaurant.setDescription(restaurantRegisterForm.getDescription());
		restaurant.setLowestPrice(restaurantRegisterForm.getLowestPrice());
		restaurant.setHighestPrice(restaurantRegisterForm.getHighestPrice());
		restaurant.setPostalCode(restaurantRegisterForm.getPostalCode());
		restaurant.setAddress(restaurantRegisterForm.getAddress());
		restaurant.setOpeningTime(restaurantRegisterForm.getOpeningTime());
		restaurant.setClosingTime(restaurantRegisterForm.getClosingTime());
		restaurant.setSeatingCapacity(restaurantRegisterForm.getSeatingCapacity());

		restaurantRepository.save(restaurant);

		if (regularHolidayIds != null) {
			regularHolidayRestaurantService.create(regularHolidayIds, restaurant);
		}

		if (categoryIds != null) {
			categoryRestaurantService.create(categoryIds, restaurant);
		}
	}

	@Transactional
	public void update(RestaurantEditForm restaurantEditForm) {
		Restaurant restaurant = restaurantRepository.getReferenceById(restaurantEditForm.getId());
		MultipartFile image = restaurantEditForm.getImage();
		List<Integer> regularHolidayIds = restaurantEditForm.getRegularHolidayIds();
		List<Integer> categoryIds = restaurantEditForm.getCategoryIds();

		if (!image.isEmpty()) {
			String imageName = image.getOriginalFilename();
			String hashedImageName = generateNewFileName(imageName);
			Path filePath = Paths.get("src/main/resources/static/storage/" + hashedImageName);
			copyImageFile(image, filePath);
			restaurant.setImage(hashedImageName);
		}

		restaurant.setName(restaurantEditForm.getName());
		restaurant.setDescription(restaurantEditForm.getDescription());
		restaurant.setLowestPrice(restaurantEditForm.getLowestPrice());
		restaurant.setHighestPrice(restaurantEditForm.getHighestPrice());
		restaurant.setPostalCode(restaurantEditForm.getPostalCode());
		restaurant.setAddress(restaurantEditForm.getAddress());
		restaurant.setOpeningTime(restaurantEditForm.getOpeningTime());
		restaurant.setClosingTime(restaurantEditForm.getClosingTime());
		restaurant.setSeatingCapacity(restaurantEditForm.getSeatingCapacity());

		restaurantRepository.save(restaurant);

		regularHolidayRestaurantService.update(regularHolidayIds, restaurant);
		categoryRestaurantService.update(categoryIds, restaurant);
	}

	@Transactional
	public void delete(Restaurant restaurant) {
		regularHolidayRestaurantService.deleteByRestaurant(restaurant);
		categoryRestaurantService.deleteByRestaurant(restaurant);
		favoriteService.deleteByRestaurant(restaurant);
		reservationService.deleteByRestaurant(restaurant);
		reviewService.deleteByRestaurant(restaurant);
		restaurantRepository.delete(restaurant);
	}

	// UUIDを使って生成したファイル名を返す
	public String generateNewFileName(String fileName) {
		String[] fileNames = fileName.split("\\.");
		for (int i = 0; i < fileNames.length - 1; i++) {
			fileNames[i] = UUID.randomUUID().toString();
		}
		String hashedFileName = String.join(".", fileNames);
		return hashedFileName;
	}

	// 画像ファイルを指定したファイルにコピーする
	public void copyImageFile(MultipartFile image, Path filePath) {
		try {
			Files.copy(image.getInputStream(), filePath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 価格が正しく設定されているかどうかをチェックする
	public boolean isValidPrices(Integer lowestPrice, Integer highestPrice) {
		return highestPrice >= lowestPrice;
	}

	// 営業時間が正しく設定されているかどうかをチェックする
	public boolean isValidBusinessHours(LocalTime openingTime, LocalTime closingTime) {
		return closingTime.isAfter(openingTime);
	}

	// 掲載日（データの作成日）が新しい順に並べ替える
	public List<Restaurant> sortByCreatedAtDesc(List<Restaurant> restaurants) {
		restaurants.sort((r1, r2) -> r2.getCreatedAt().compareTo(r1.getCreatedAt()));
		return restaurants;
	}

	// 価格（最低価格と最高価格の平均）が安い順に並べ替える
	public List<Restaurant> sortByPriceAsc(List<Restaurant> restaurants) {
		restaurants.sort(Comparator.comparingDouble(r -> (r.getLowestPrice() + r.getHighestPrice()) / 2.0));

		return restaurants;
	}

	// 評価が高い順に並べ替える（平均評価や標準偏差を使った独自のアルゴリズム）
	public List<Restaurant> sortByRatingDesc(List<Restaurant> restaurants) {
		Double averageScore = restaurantRepository.findAverageScore();
		Double standardDeviation = restaurantRepository.findStandardDeviation();

		// averageScore と standardDeviation が null の場合、そのまま restaurants を返す
		if (averageScore == null || standardDeviation == null) {
			return restaurants;
		}

		// averageScore と standardDeviation が null でない場合にソートを行う
		restaurants.sort((r1, r2) -> {
			double rating1 = ((r1.getAverageScore() - averageScore) * 10 / standardDeviation) * r1.getReviews().size();
			double rating2 = ((r2.getAverageScore() - averageScore) * 10 / standardDeviation) * r2.getReviews().size();
			return Double.compare(rating2, rating1);
		});

		return restaurants;
	}
}
