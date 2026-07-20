package com.mjc.hotel.promotionSale;

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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PromotionSaleRestController.class)
public class TestPromotionSaleRestController {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PromotionSaleService promotionSaleService;

    @Autowired
    private ObjectMapper objectMapper;

    private static PromotionSaleDto samplePromotionSaleDto;

    @BeforeEach
    void setUp() {

        samplePromotionSaleDto = PromotionSaleDto.builder()
                .proSaleId(1L)
                .saleDes("객실 업그레이드")
                .proId(10L)
                .userId(2L)
                .build();
    }

    @Test
    @DisplayName("GET /api/promotionsale - 프로모션 할인 목록 조회")
    void getPromotionSale_shouldReturnOk() throws Exception {

        Page<PromotionSaleDto> page =
                new PageImpl<>(List.of(samplePromotionSaleDto),
                        PageRequest.of(0, 10), 1);

        when(promotionSaleService.findAll(any())).thenReturn(page);

        mockMvc.perform(get("/api/promotionsale")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseData.content[0].proSaleId").value(1))
                .andExpect(jsonPath("$.responseData.content[0].saleDes").value("객실 업그레이드"))
                .andExpect(jsonPath("$.responseData.content[0].proId").value(10))
                .andExpect(jsonPath("$.responseData.totalElements").value(1));

        verify(promotionSaleService, times(1)).findAll(any());
    }

    @Test
    @DisplayName("GET /api/promotionsale/{proSaleId} - 단건 조회")
    void getPromotionSaleById_shouldReturnOk() throws Exception {

        when(promotionSaleService.findById(1L))
                .thenReturn(samplePromotionSaleDto);

        mockMvc.perform(get("/api/promotionsale/{proSaleId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseData.proSaleId").value(1))
                .andExpect(jsonPath("$.responseData.saleDes").value("객실 업그레이드"))
                .andExpect(jsonPath("$.responseData.proId").value(10));

        verify(promotionSaleService, times(1)).findById(1L);
    }

    @Test
    @DisplayName("POST /api/promotionsale - 등록")
    void insertPromotionSale_shouldReturnCreated() throws Exception {

        when(promotionSaleService.insert(any(PromotionSaleDto.class)))
                .thenReturn(samplePromotionSaleDto);

        mockMvc.perform(post("/api/promotionsale")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(samplePromotionSaleDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.responseData.proSaleId").value(1))
                .andExpect(jsonPath("$.responseData.saleDes").value("객실 업그레이드"));

        verify(promotionSaleService, times(1))
                .insert(any(PromotionSaleDto.class));
    }

    @Test
    @DisplayName("PATCH /api/promotionsale - 수정")
    void updatePromotionSale_shouldReturnOk() throws Exception {

        samplePromotionSaleDto.setSaleDes("무료 조식 제공");

        when(promotionSaleService.update(any(PromotionSaleDto.class)))
                .thenReturn(samplePromotionSaleDto);

        mockMvc.perform(patch("/api/promotionsale")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(samplePromotionSaleDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseData.saleDes").value("무료 조식 제공"));

        verify(promotionSaleService, times(1))
                .update(any(PromotionSaleDto.class));
    }

    @Test
    @DisplayName("DELETE /api/promotionsale/{proSaleId} - 삭제")
    void deletePromotionSale_shouldReturnOk() throws Exception {

        when(promotionSaleService.deleteById(1L, 2L))
                .thenReturn(samplePromotionSaleDto);

        mockMvc.perform(delete("/api/promotionsale/{proSaleId}", 1L)
                        .param("userId", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseData.proSaleId").value(1));

        verify(promotionSaleService, times(1))
                .deleteById(1L, 2L);
    }

    @Test
    @DisplayName("GET /api/promotionsale/promotion/{proId} - 프로모션별 할인 목록 조회")
    void getPromotionSaleByPromotion_shouldReturnOk() throws Exception {

        Page<PromotionSaleDto> page =
                new PageImpl<>(List.of(samplePromotionSaleDto),
                        PageRequest.of(0, 10), 1);

        when(promotionSaleService.findAllByProIdEquals(eq(10L), any()))
                .thenReturn(page);

        mockMvc.perform(get("/api/promotionsale/promotion/{proId}", 10L)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseData.content[0].proSaleId").value(1))
                .andExpect(jsonPath("$.responseData.content[0].proId").value(10))
                .andExpect(jsonPath("$.responseData.totalElements").value(1));

        verify(promotionSaleService, times(1))
                .findAllByProIdEquals(eq(10L), any());
    }
}