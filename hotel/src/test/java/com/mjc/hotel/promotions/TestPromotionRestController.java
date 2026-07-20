package com.mjc.hotel.promotions;

import com.mjc.hotel.promotion.DiscountTypeEnum;
import com.mjc.hotel.promotion.PromotionDto;
import com.mjc.hotel.promotion.PromotionRestController;
import com.mjc.hotel.promotion.PromotionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PromotionRestController.class)
public class TestPromotionRestController {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PromotionService promotionService;

    @Autowired
    private ObjectMapper objectMapper;

    private static PromotionDto samplePromotionDto;

    @BeforeEach
    void setUp() {

        samplePromotionDto = PromotionDto.builder()
                .proId(1L)
                .name("여름 할인")
                .description("여름 20% 할인")
                .disType(DiscountTypeEnum.RATE)
                .disValue("20")
                .startDate(LocalDateTime.of(2026,7,20,0,0))
                .endDate(LocalDateTime.of(2026,7,31,23,59))
                .resCount(0)
                .status("ACTIVE")
                .roomId(1L)
                .userId(2L)
                .build();
    }

    @Test
    @DisplayName("GET /api/promotion - 프로모션 목록 조회")
    void getPromotion_shouldReturnOk() throws Exception {

        Page<PromotionDto> page =
                new PageImpl<>(List.of(samplePromotionDto),
                        PageRequest.of(0,10),1);

        when(promotionService.findAll(any())).thenReturn(page);

        mockMvc.perform(get("/api/promotion")
                        .param("page","0")
                        .param("size","10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseData.content[0].proId").value(1))
                .andExpect(jsonPath("$.responseData.content[0].name").value("여름 할인"))
                .andExpect(jsonPath("$.responseData.totalElements").value(1));

        verify(promotionService,times(1)).findAll(any());
    }

    @Test
    @DisplayName("GET /api/promotion/{proId} - 단건 조회")
    void getPromotionById_shouldReturnOk() throws Exception {

        when(promotionService.findById(1L))
                .thenReturn(samplePromotionDto);

        mockMvc.perform(get("/api/promotion/{proId}",1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseData.proId").value(1))
                .andExpect(jsonPath("$.responseData.name").value("여름 할인"));

        verify(promotionService,times(1)).findById(1L);
    }

    @Test
    @DisplayName("POST /api/promotion - 등록")
    void insertPromotion_shouldReturnCreated() throws Exception {

        when(promotionService.insert(any(PromotionDto.class)))
                .thenReturn(samplePromotionDto);

        mockMvc.perform(post("/api/promotion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(samplePromotionDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.responseData.proId").value(1))
                .andExpect(jsonPath("$.responseData.name").value("여름 할인"));

        verify(promotionService,times(1))
                .insert(any(PromotionDto.class));
    }

    @Test
    @DisplayName("PATCH /api/promotion - 수정")
    void updatePromotion_shouldReturnOk() throws Exception {

        samplePromotionDto.setDisValue("30");

        when(promotionService.update(any(PromotionDto.class)))
                .thenReturn(samplePromotionDto);

        mockMvc.perform(patch("/api/promotion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(samplePromotionDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseData.disValue").value("30"));

        verify(promotionService,times(1))
                .update(any(PromotionDto.class));
    }

    @Test
    @DisplayName("DELETE /api/promotion/{proId} - 삭제")
    void deletePromotion_shouldReturnOk() throws Exception {

        when(promotionService.deleteById(1L,2L))
                .thenReturn(samplePromotionDto);

        mockMvc.perform(delete("/api/promotion/{proId}",1L)
                        .param("userId","2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseData.proId").value(1));

        verify(promotionService,times(1))
                .deleteById(1L,2L);
    }

    @Test
    @DisplayName("GET /api/promotion/room/{roomId} - 객실별 프로모션 조회")
    void getPromotionByRoom_shouldReturnOk() throws Exception {

        Page<PromotionDto> page =
                new PageImpl<>(List.of(samplePromotionDto),
                        PageRequest.of(0,10),1);

        when(promotionService.findAllByRoomIdEquals(eq(1L), any()))
                .thenReturn(page);

        mockMvc.perform(get("/api/promotion/room/{roomId}",1L)
                        .param("page","0")
                        .param("size","10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseData.content[0].roomId").value(1))
                .andExpect(jsonPath("$.responseData.totalElements").value(1));

        verify(promotionService,times(1))
                .findAllByRoomIdEquals(eq(1L), any());
    }
}