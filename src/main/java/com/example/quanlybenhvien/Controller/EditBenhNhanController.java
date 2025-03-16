package com.example.quanlybenhvien.Controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.quanlybenhvien.Entity.BenhNhan;
import com.example.quanlybenhvien.Service.BenhNhanService;

import jakarta.persistence.EntityNotFoundException;

@Controller
@RequestMapping("/benhnhan")
public class EditBenhNhanController {

    @Autowired
    private BenhNhanService benhNhanService;

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Integer id, Model model) {
        BenhNhan benhNhan = benhNhanService.findById(id);
        if (benhNhan != null) {
            // Kiểm tra nếu namSinh đang null, đặt giá trị mặc định
            if (benhNhan.getNamSinh() == null) {
                benhNhan.setNamSinh(LocalDate.now()); // Đặt ngày mặc định là hôm nay
            }
            model.addAttribute("benhnhan", benhNhan);
            return "thongtincanhan";
        }
        model.addAttribute("error", "Bệnh nhân không tồn tại");
        return "error";
    }

    @PostMapping("/update/{id}")
    public String updateBenhNhan(@PathVariable("id") Integer id, 
                                 @ModelAttribute BenhNhan benhNhan, 
                                 @RequestParam("file") MultipartFile file, 
                                 RedirectAttributes redirectAttributes) {
        try {
            // Lấy dữ liệu bệnh nhân từ database
            BenhNhan existingBenhNhan = benhNhanService.findById(id);
            if (existingBenhNhan == null) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy bệnh nhân!");
                return "redirect:/benhnhan";
            }
    
            // Cập nhật thông tin bệnh nhân
            existingBenhNhan.setHoTen(benhNhan.getHoTen());
            existingBenhNhan.setDuong(benhNhan.getDuong());
            existingBenhNhan.setQuanHuyen(benhNhan.getQuanHuyen());
            existingBenhNhan.setEmail(benhNhan.getEmail());
            existingBenhNhan.setGioiTinh(benhNhan.getGioiTinh());
            existingBenhNhan.setHinh(benhNhan.getHinh());
            existingBenhNhan.setNamSinh(benhNhan.getNamSinh());
    
            // Nếu có file ảnh được tải lên
            if (file != null && !file.isEmpty()) {
                try {
                    // Tạo tên file duy nhất
                    String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                    String uploadDir = new File("src/main/resources/static/images/").getAbsolutePath();

                    // Tạo thư mục nếu chưa tồn tại
                    File uploadFolder = new File(uploadDir);
                    if (!uploadFolder.exists()) {
                        uploadFolder.mkdirs();
                    }

                    // Lưu file vào thư mục
                    File destinationFile = new File(uploadFolder, fileName);
                    file.transferTo(destinationFile);

                    // Xóa ảnh cũ nếu có
                    if (existingBenhNhan.getHinh() != null && !existingBenhNhan.getHinh().equals("default.png")) {
                        File oldFile = new File(uploadFolder, existingBenhNhan.getHinh());
                        if (oldFile.exists()) {
                            oldFile.delete();
                        }
                    }

                    // Cập nhật ảnh mới vào database
                    existingBenhNhan.setHinh(fileName);
                } 
                catch (Exception e) {
    redirectAttributes.addFlashAttribute("error", "Lỗi khi tải ảnh lên!");
    return "redirect:/admin/bacsi"; // Dùng redirect thay vì return view trực tiếp
}

            }

    
            // Lưu bệnh nhân vào database
            benhNhanService.updateBenhNhan(id, existingBenhNhan);
            redirectAttributes.addFlashAttribute("message", "Cập nhật thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi cập nhật thông tin!");
            e.printStackTrace();
        }
    
        return "redirect:/benhnhan/edit/" + id;
    }
    

}
