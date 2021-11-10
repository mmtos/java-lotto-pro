package model;

import static java.util.stream.Collectors.*;
import static model.CommonString.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.IntStream;

public class Lotto {
	public static final Integer COST = 1000;
	public static final Integer NUMBER_COUNT = 6;
	public static final int INDEX_OF_START = 0;
	private static List<Integer> numberCandidates;

	static {
		numberCandidates = IntStream.rangeClosed(LottoNumber.MIN_NUMBER, LottoNumber.MAX_NUMBER)
			.boxed()
			.collect(toList());
	}

	private Set<LottoNumber> numbers;

	public Lotto(List<Integer> numbers) {
		this.numbers = numbers.stream()
			.map(LottoNumber::of)
			.collect(toCollection(TreeSet::new));
	}

	public Lotto(Set<LottoNumber> numbers) {
		this.numbers = numbers;
	}

	public static Lotto of(String numbersWithComma) {
		Set<Integer> numbers = Arrays.stream(splitToEachNumber(numbersWithComma))
			.map(Integer::parseInt)
			.collect(toSet());

		return Lotto.createByManual(numbers);
	}

	public static Lotto createByAuto() {
		Collections.shuffle(numberCandidates);

		List<Integer> selectedNumbers = numberCandidates.subList(INDEX_OF_START, INDEX_OF_START + NUMBER_COUNT);
		Collections.sort(selectedNumbers);

		return new Lotto(selectedNumbers);
	}

	public static Lotto createByManual(Set<Integer> numbers) {
		Set<LottoNumber> lottoNumbers = numbers.stream()
			.map(LottoNumber::of)
			.collect(toCollection(TreeSet::new));

		return new Lotto(lottoNumbers);
	}

	public static boolean isNotDuplicatedNumber(Set<Integer> numbers) {
		return numbers.size() == NUMBER_COUNT;
	}

	public static boolean isValidLottoNumber(Set<Integer> numbers) {
		return numbers.stream()
				.allMatch(LottoNumber::isValidNumber);
	}

	public Count matchCount(Lotto other) {
		Count count = Count.zero();

		for (LottoNumber number : other.getNumbers()) {
			count = numbers.contains(number) ? Count.sum(count, Count.one()) : count;
		}

		return count;
	}

	public boolean isMatchBonusBall(BonusBall bonusBall) {
		return isContains(bonusBall.getNumber());
	}

	public boolean isNotContain(BonusBall bonusBall) {
		return !isContains(bonusBall.getNumber());
	}

	public boolean isContains(LottoNumber lottoNumber) {
		return numbers.contains(lottoNumber);
	}

	public static boolean validate(String value) {
		String[] strings = value.replaceAll(Regex.SPACE, EMPTY_STRING)
			.split(COMMA);

		if (strings.length != NUMBER_COUNT) {
			return false;
		}

		return Arrays.stream(strings)
			.allMatch(string -> string.matches(Regex.NUMBER))
			&& isValidNumber(strings);
	}

	private static boolean isValidNumber(String[] strings) {
		Set<Integer> numbers = Arrays.stream(strings)
			.map(Integer::parseInt)
			.collect(toSet());

		return isNotDuplicatedNumber(numbers)
			&& isValidLottoNumber(numbers);
	}

	private static String[] splitToEachNumber(String lastWeekWinningNumber) {
		return lastWeekWinningNumber.replaceAll(Regex.SPACE, EMPTY_STRING)
			.split(COMMA);
	}

	public Set<LottoNumber> getNumbers() {
		return numbers;
	}
}