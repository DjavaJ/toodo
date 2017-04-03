package com.theironyard.novauc.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A Tasks.
 */
@Entity
@Table(name = "tasks")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Tasks implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "is_done")
    private Boolean isDone;

    @Column(name = "item")
    private String item;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public Tasks name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean isIsDone() {
        return isDone;
    }

    public Tasks isDone(Boolean isDone) {
        this.isDone = isDone;
        return this;
    }

    public void setIsDone(Boolean isDone) {
        this.isDone = isDone;
    }

    public String getItem() {
        return item;
    }

    public Tasks item(String item) {
        this.item = item;
        return this;
    }

    public void setItem(String item) {
        this.item = item;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Tasks tasks = (Tasks) o;
        if (tasks.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, tasks.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Tasks{" +
            "id=" + id +
            ", name='" + name + "'" +
            ", isDone='" + isDone + "'" +
            ", item='" + item + "'" +
            '}';
    }
}
