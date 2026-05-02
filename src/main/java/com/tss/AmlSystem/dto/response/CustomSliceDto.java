package com.tss.AmlSystem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Slice;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomSliceDto<T> {
    private List<T> content;
    private boolean last;
    private int numberOfElements;
    public CustomSliceDto(Slice<T> slice) {
        this.content = slice.getContent();
        this.last = slice.isLast();
        this.numberOfElements = slice.getNumberOfElements();
    }
}
