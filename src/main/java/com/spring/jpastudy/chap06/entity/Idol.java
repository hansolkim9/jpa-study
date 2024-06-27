package com.spring.jpastudy.chap06.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Setter @Getter
@ToString(exclude = "group")
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Idol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idol_id")
    private Long id;

    private String idolName;

    private int age;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Group group;


    public Idol(String idolName, int age, Group group) {
        this.idolName = idolName;
        this.age = age;
        if (group != null) {
            changeGroup(group);
        }
    }


    public void changeGroup(Group group) {
        this.group = group;
        group.getIdols().add(this);
    }
}
