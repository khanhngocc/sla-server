package com.g18.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User owner;
    private String name;

    private String description;
    private Instant createdDate;
    private Instant updateDate;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL)
    private List<RoomMember> roomMembers; //list members of room

    @OneToMany(mappedBy = "room")
    private List<RoomStudySet>  roomStudySets; //list study sets in room

    @OneToMany(mappedBy = "room")
    private List<RoomFolder>  roomFolders; //list folders in room
}
