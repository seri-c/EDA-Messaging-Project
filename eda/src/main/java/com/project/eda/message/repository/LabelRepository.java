package com.project.eda.message.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.eda.message.entity.Label;

public interface LabelRepository extends JpaRepository<Label, String>{

    
} 
