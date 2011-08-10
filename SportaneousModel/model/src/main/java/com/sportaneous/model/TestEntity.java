package com.sportaneous.model;

import java.io.*;
import java.util.*;
import javax.persistence.*;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: reuben
 * Date: 7/18/11
 * Time: 8:03 AM
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "testentities")
class TestEntity implements Serializable
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "game_id")
    private Long gameID;
}