package com.example.yaksokServer.service;

import com.example.yaksokServer.model.Pill;
import com.example.yaksokServer.model.PillInfo;
import com.example.yaksokServer.repository.PillRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class DBService {
    private final PillRepository pillRepository;

    public String insert(Pill pill){
        pillRepository.insert(pill);
        return pill.getItemSeq();
    }

    public Pill findBySeq(String seq){
        return pillRepository.search(seq);
    }

    public Pill update(Pill pill){
        pillRepository.update(pill);
        return pill;
    }

    public String delete(Pill pill){
        pillRepository.delete(pill.getItemSeq());
        return pill.getItemSeq();
    }

    public PillInfo pillToPillInfo(Pill pill) {
        PillInfo pillInfo = new PillInfo();

        pillInfo.setItemSeq(pill.getItemSeq());
        pillInfo.setEntpName(pill.getEntpName());
        pillInfo.setItemName(pill.getItemName());
        pillInfo.setEfcyQesitm(pill.getEfcyQesitm());
        pillInfo.setUseMethodQesitm(pill.getUseMethodQesitm());
        pillInfo.setAtpnWarnQesitm(pill.getAtpnWarnQesitm());
        pillInfo.setAtpnQesitm(pill.getAtpnQesitm());
        pillInfo.setIntrcQesitm(pill.getIntrcQesitm());
        pillInfo.setSeQesitm(pill.getSeQesitm());
        pillInfo.setDepositMethodQesitm(pill.getDepositMethodQesitm());
        pillInfo.setOpenDe(pill.getOpenDe());
        pillInfo.setUpdateDe(pill.getUpdateDe());
        pillInfo.setItemImage(pill.getItemImage());
        pillInfo.setBizrno(pill.getBizrno());

        return pillInfo;
    }

}
