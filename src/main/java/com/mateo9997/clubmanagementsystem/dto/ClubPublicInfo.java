package com.mateo9997.clubmanagementsystem.dto;


public class ClubPublicInfo {
    private String officialName;
    private String popularName;
    private String federation;
    private boolean isPublic;
    private int numberOfPlayers;

    public ClubPublicInfo(String officialName, String popularName, String federation, boolean isPublic, int numberOfPlayers) {
        this.officialName = officialName;
        this.popularName = popularName;
        this.federation = federation;
        this.isPublic = isPublic;
    }

    public String getOfficialName() {
        return officialName;
    }

    public void setOfficialName(String officialName) {
        this.officialName = officialName;
    }

    public String getPopularName() {
        return popularName;
    }

    public void setPopularName(String popularName) {
        this.popularName = popularName;
    }

    public String getFederation() {
        return federation;
    }

    public void setFederation(String federation) {
        this.federation = federation;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

}
