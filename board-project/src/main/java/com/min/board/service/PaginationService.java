package com.min.board.service;



import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.IntStream;

@Service
public class PaginationService {
    private static final int BAR_LENGTH = 5;    //페이지 기본값
    public List<Integer> getPaginationBarNumbers(int currentPageNumber, int totalPages) {   //현재 페이지와 총 페이지는 결국 마지막 페이지
        int startNumber = Math.max(currentPageNumber - (BAR_LENGTH/2),0);
        int endNumber = Math.min(startNumber + BAR_LENGTH, totalPages);

        return IntStream.range(startNumber,endNumber).boxed().toList();
    }

    public int currentBarLength() {
        return BAR_LENGTH;
    }
}
