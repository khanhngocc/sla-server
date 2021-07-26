package com.g18.service;

import com.g18.dto.*;
import com.g18.entity.*;
import com.g18.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class SearchService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private FolderRepository folderRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private StudySetRepository studySetRepository;

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private FolderStudySetRepository folderStudySetRepository;


    public Page<SearchStudySetResponse> searchStudySetByTitle(Pageable pageable, String keySearch) {
        Page<StudySet> studySetPage =studySetRepository.findByTitleContainsAndIsPublicTrue(keySearch,pageable);
        int totalElements = (int) studySetPage.getTotalElements();
        return new PageImpl<SearchStudySetResponse>(
                studySetPage.stream().map(studySet -> new SearchStudySetResponse(
                          studySet.getId(),
                        findUserNameByUserId(studySet.getCreator().getId()),
                        studySet.getTitle(),
                        studySet.getDescription(),
                        convertToCardDto(cardRepository.findTop4ByStudySetId(studySet.getId())),
                        studySet.getTag(),
                        studySet.getCards().size(),
                        studySet.getCreatedDate()
                        )
                ).collect(Collectors.toList()), pageable, totalElements);
    }

    //Search list public Study set by tag
    public Page<SearchStudySetResponse> searchStudySetByTag(Pageable pageable, String keySearch) {
        Page<StudySet> studySetPage =studySetRepository.findByTagContainsAndIsPublicTrue(keySearch,pageable);
        int totalElements = (int) studySetPage.getTotalElements();
        return new PageImpl<SearchStudySetResponse>(
                studySetPage.stream().map(studySet -> new SearchStudySetResponse(
                                studySet.getId(),
                        findUserNameByUserId(studySet.getCreator().getId()),
                                studySet.getTitle(),
                                studySet.getDescription(),
                                convertToCardDto(cardRepository.findTop4ByStudySetId(studySet.getId())),
                                studySet.getTag(),
                                studySet.getCards().size(),
                                studySet.getCreatedDate()
                        )
                ).collect(Collectors.toList()), pageable, totalElements);
    }


    public Page<EventDto> searchEventByTitle(Pageable pageable, String nameSearch) {
        Page<Event> eventPage =eventRepository.findByNameContaining(nameSearch,pageable);
        int totalElements = (int) eventPage.getTotalElements();
        return new PageImpl<EventDto>(
                eventPage.stream().map(event -> new EventDto(
                        event.getId(),event.getUser().getId(),event.getName(),event.getDescription(),event.isLearnEvent(),
                        event.getFromTime().toString(), event.getToTime().toString(),event.getColor(),event.getCreatedTime().toString(),event.getUpdateTime().toString()
                        )
                ).collect(Collectors.toList()), pageable, totalElements);
    }

    public Page<SearchFolderResponse> searchFolderByTitle(Pageable pageable, String titleSearch) {
        Page<Folder> folderPage = folderRepository.findByTitleContaining(titleSearch,pageable);
        int totalElements = folderPage.getNumberOfElements();
        return new PageImpl<SearchFolderResponse>(
                folderPage.stream().map(
                        folder -> new SearchFolderResponse(
                                folder.getId(),
                                findUserNameByUserId(folder.getOwner().getId()),
                                folder.getTitle(),
                                folder.getDescription(),
                                folder.getCreatedDate(),
                                folder.getCreatedDate(),
                                folder.getColor(),
                                folder.getFolderStudySets().size()
                        )
                ).collect(Collectors.toList()), pageable, totalElements);
    }
    //Search user by username and paging
    public Page<SearchUserResponse> searchUserByUsername(Pageable pageable, String username) {
        Page<Account> accountPage = accountRepository.findByUsernameContains(username,pageable);
        int totalElements = accountPage.getNumberOfElements();
        return new PageImpl<SearchUserResponse>(
                accountPage.stream().map(
                        account -> new SearchUserResponse(
                                account.getUsername(),
                                account.getUser().getAvatar(),
                                account.getUser().getAvatar(),
                                account.getUser().getStudySetsOwn().size()
                        )
                ).collect(Collectors.toList()), pageable, totalElements);
    }

    // Search room by name and paging
    public Page<SearchRoomResponse> searchRoomByName(Pageable pageable, String nameSearch) {
        Page<Room> roomPage = roomRepository.findByNameContaining(nameSearch,pageable);
        int totalElements = roomPage.getNumberOfElements();
        return new PageImpl<SearchRoomResponse>(
                roomPage.stream().map(
                        room -> new SearchRoomResponse(
                            room.getId(),
                            findUserNameByUserId(room.getOwner().getId()),
                                room.getName(),
                                room.getDescription(),
                                room.getCreatedDate(),
                                room.getUpdateDate(),
                                //Error when find number of member (memberId and roomID)
                                room.getRoomMembers().size(),
                                //Error when find number of studyset (memberId and roomID)
                                getTotalStudySetsInRoom(room)
                        )
                ).collect(Collectors.toList()), pageable, totalElements);
    }

    private int getTotalStudySetsInRoom(Room room){
       List<Long> ssid = folderStudySetRepository.findNumberSSID("folder_id = 1 or folder_id = 2" +"folde =1");
       for (Long s : ssid){
           System.out.println("SSID: " + s);
       }
    return ssid.size();
    }

    public String findUserNameByUserId(long uid){
        return accountRepository.findUserNameByUserId(uid);
    }

    private List<CardSearchResponse> convertToCardDto(List<Card> cards){
        List<CardSearchResponse> cardsDto = new ArrayList<>();
        for (Card c : cards){
            CardSearchResponse cDto = new CardSearchResponse();
            cDto.setId(c.getId());
            cDto.setFront(c.getFront());
            cDto.setBack(c.getBack());
            cDto.setStudySetID(c.getStudySet().getId());
            cardsDto.add(cDto);
        }
        return cardsDto;
    }

}
