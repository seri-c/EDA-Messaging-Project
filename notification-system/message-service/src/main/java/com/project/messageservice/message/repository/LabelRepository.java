package com.project.messageservice.message.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.messageservice.message.entity.Label;

public interface LabelRepository extends JpaRepository<Label, String>{

    
} 
