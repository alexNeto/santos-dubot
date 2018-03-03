package com.admin.tafmetar.model;

import com.admin.tafmetar.Enum.TargetType;
import com.admin.tafmetar.shared.BusinessException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class TafMetarAerodromeService {

    private String protocol = "http://";
    private String urlStem = "www.redemet.aer.mil.br/api/consulta_automatica/index.php?local=";
    private String urlTaf = "&msg=metar&data_ini=";
    private String urlMetar = "&msg=taf&data_ini=";
    private String urlAerodromeInfo = "&msg=aviso_aerodromo&data_ini=";

    private List<TargetType> targetList;
    private String locale;

    public TafMetarAerodromeService(String locale) {
        this.locale = locale;
    }

    public String buildUrl(TargetType target) {
        StringBuffer urlBuilder = new StringBuffer();
        urlBuilder.append(this.protocol);
        urlBuilder.append(this.urlStem);
        if (this.locale == null || this.locale.isEmpty()) {
            throw new BusinessException("O local está vazio"); // TODO - adicionar I18N
        } else {
            urlBuilder.append(locale);
        }
        if (target == null) {
            throw new BusinessException("O modo desejado deve ser informado"); // TODO - adicionar I18N
        } else {
            if (target == TargetType.TAF) {
                urlBuilder.append(urlTaf);
            }
            if (target == TargetType.METAR) {
                urlBuilder.append(urlMetar);
            }
            if (target == TargetType.AERODROME) {
                urlBuilder.append(urlAerodromeInfo);
            }
        }
        return urlBuilder.toString();
    }

    public List<String> getResponse() {
        String url;
        List<String> response = new ArrayList<>();
        for (TargetType target : targetList) {
            url = this.buildUrl(target);
            response.add(this.makeRequest(url));
        }
        return response;
    }

    public String makeRequest(String urlStr) {
        String partialResponse;
        BufferedReader reader = null;
        try {
            URL url = new URL(urlStr);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            partialResponse = reader.readLine();
        } catch (Exception e) {
            throw new BusinessException("Não conseguimos acessar os dados desejados, tente novamente"); // TODO - adicionar I18N
        } finally {
            try {
                reader.close();
            } catch (Exception e) {
                throw new BusinessException("Algo deu muito errado, tente de novo"); // TODO - adicionar I18N
            }
        }
        return partialResponse; // TODO - implements
    }

    public void setTargetList(List<TargetType> targetList) {
        this.targetList = targetList;
    }
}