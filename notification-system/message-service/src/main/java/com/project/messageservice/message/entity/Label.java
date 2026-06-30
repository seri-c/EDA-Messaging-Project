package com.project.messageservice.message.entity;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="labels")
public class Label {
    @Id
    @Column(name = "name", length = 100)
    private String name;


    public Label(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }



    @Override
    public boolean equals(Object other){

        if(this == other) return true;

        if(!(other instanceof Label)) return false;

        Label otherLabel = (Label) other;
        return Objects.equals(name, otherLabel.name);
    }

    @Override
    public int hashCode(){
        return Objects.hash(name);
    }

}
