package com.codey.fatcat.controller;

import com.codey.fatcat.dto.BillDTO;
import com.codey.fatcat.entity.Bill;
import com.codey.fatcat.service.BillService;
import com.codey.fatcat.utils.DTOConverter;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/bills")
public class BillController {

    private final BillService billService;

    public BillController(BillService billService) {
        this.billService = billService;
    }

    @GetMapping
    public ResponseEntity<List<BillDTO>> getAllBills() {
        List<Bill> bills = billService.getAllBills();
        return ResponseEntity.ok(DTOConverter.toList(bills, DTOConverter::convertToDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BillDTO> getBillById(@PathVariable UUID id) {
        Bill bill = billService.getBillById(id);
        return ResponseEntity.ok(DTOConverter.convertToDTO(bill));
    }

    @GetMapping("/upcoming")
    public ResponseEntity<List<BillDTO>> getUpcomingBills() {
        List<Bill> bills = billService.getUpcomingBills();
        return ResponseEntity.ok(DTOConverter.toList(bills, DTOConverter::convertToDTO));
    }

    @PostMapping
    public ResponseEntity<BillDTO> createBill(@Valid @RequestBody BillDTO billDTO) {
        Bill bill = billService.createBill(billDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(DTOConverter.convertToDTO(bill));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BillDTO> updateBill(@PathVariable UUID id, @Valid @RequestBody BillDTO billDTO) {
        Bill bill = billService.updateBill(id, billDTO);
        return ResponseEntity.accepted().body(DTOConverter.convertToDTO(bill));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BillDTO> deleteBill(@PathVariable UUID id) {
        return billService.deleteBill(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

}
