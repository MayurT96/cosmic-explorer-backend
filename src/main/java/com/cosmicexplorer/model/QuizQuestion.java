package com.cosmicexplorer.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;

/** One multiple-choice question. JSON keys (q, o, a, e) match the React app. */
@Entity
@Table(name = "quiz_question")
public class QuizQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int sortOrder;

    /** question text */
    @Column(name = "question", length = 300)
    private String q;

    /** the answer options, in order */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "quiz_option", joinColumns = @JoinColumn(name = "question_id"))
    @OrderColumn(name = "pos")
    @Column(name = "option_text", length = 200)
    private List<String> o = new ArrayList<>();

    /** index of the correct option */
    @Column(name = "answer_index")
    private int a;

    /** explanation shown after answering */
    @Column(name = "explanation", length = 600)
    private String e;

    protected QuizQuestion() {
    }

    @JsonIgnore
    public Long getId() { return id; }
    @JsonIgnore
    public int getSortOrder() { return sortOrder; }
    public void setSortOrder(int sortOrder) { this.sortOrder = sortOrder; }
    public String getQ() { return q; }
    public List<String> getO() { return o; }
    public int getA() { return a; }
    public String getE() { return e; }
}
